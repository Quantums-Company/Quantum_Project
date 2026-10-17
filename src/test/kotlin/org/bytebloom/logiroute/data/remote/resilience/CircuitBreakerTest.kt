package org.bytebloom.logiroute.data.remote.resilience

import kotlinx.coroutines.runBlocking
import org.bytebloom.logiroute.data.remote.exception.CallNotPermittedException
import java.io.IOException
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class CircuitBreakerTest {

    private var nowNanos = 0L

    private fun createBreaker() = CircuitBreaker(
        failureThreshold = 5,
        openDuration = Duration.ofSeconds(5),
        isNetworkFailure = { error ->
            generateSequence(error) { it.cause }
                .any { it is IOException }
        },
        clockNanos = { nowNanos }
    )

    @Test
    fun `starts closed and allows successful calls`() = runBlocking {
        val breaker = createBreaker()

        assertEquals(CircuitState.CLOSED, breaker.currentState)
        assertEquals("OK", breaker.execute { "OK" })
        assertEquals(CircuitState.CLOSED, breaker.currentState)
    }

    @Test
    fun `opens after five consecutive network failures`() = runBlocking {
        val breaker = createBreaker()

        repeat(5) {
            assertFailsWith<IOException> {
                breaker.execute<String> {
                    throw IOException("Network failure")
                }
            }
        }

        assertEquals(CircuitState.OPEN, breaker.currentState)
    }

    @Test
    fun `open circuit rejects calls without running block`() = runBlocking {
        val breaker = createBreaker()

        repeat(5) {
            assertFailsWith<IOException> {
                breaker.execute<String> {
                    throw IOException("Network failure")
                }
            }
        }

        var blockCalled = false

        assertFailsWith<CallNotPermittedException> {
            breaker.execute {
                blockCalled = true
                "Should not run"
            }
        }

        assertFalse(blockCalled)
    }

    @Test
    fun `successful probe closes circuit after five seconds`() = runBlocking {
        val breaker = createBreaker()

        repeat(5) {
            assertFailsWith<IOException> {
                breaker.execute<String> {
                    throw IOException("Network failure")
                }
            }
        }

        nowNanos += Duration.ofSeconds(5).toNanos()

        assertEquals("Recovered", breaker.execute { "Recovered" })
        assertEquals(CircuitState.CLOSED, breaker.currentState)
    }

    @Test
    fun `failed probe reopens circuit for another five seconds`() = runBlocking {
        val breaker = createBreaker()

        repeat(5) {
            assertFailsWith<IOException> {
                breaker.execute<String> {
                    throw IOException("Network failure")
                }
            }
        }

        nowNanos += Duration.ofSeconds(5).toNanos()

        assertFailsWith<IOException> {
            breaker.execute<String> {
                throw IOException("Probe failed")
            }
        }

        assertEquals(CircuitState.OPEN, breaker.currentState)

        assertFailsWith<CallNotPermittedException> {
            breaker.execute { "Too early" }
        }

        nowNanos += Duration.ofSeconds(5).toNanos()

        assertEquals("Recovered", breaker.execute { "Recovered" })
        assertEquals(CircuitState.CLOSED, breaker.currentState)
    }

    @Test
    fun `non network failure does not count toward threshold`() = runBlocking {
        val breaker = createBreaker()

        repeat(4) {
            assertFailsWith<IOException> {
                breaker.execute<String> {
                    throw IOException("Network failure")
                }
            }
        }

        assertFailsWith<IllegalArgumentException> {
            breaker.execute<String> {
                throw IllegalArgumentException("Invalid input")
            }
        }

        assertEquals(CircuitState.CLOSED, breaker.currentState)

        // The non-network failure resets the consecutive-failure streak.
        assertFailsWith<IOException> {
            breaker.execute<String> {
                throw IOException("Network failure")
            }
        }

        assertEquals(CircuitState.CLOSED, breaker.currentState)
    }
}
