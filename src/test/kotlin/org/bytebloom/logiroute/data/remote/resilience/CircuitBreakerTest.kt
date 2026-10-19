package org.bytebloom.logiroute.data.remote.resilience

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.data.remote.exception.CallNotPermittedException
import org.bytebloom.logiroute.domain.model.exception.NetworkUnavailableException
import java.io.IOException
import java.time.Duration
import kotlin.test.Test
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
    fun `should start closed and allow successful calls`() = runTest {
        // Given
        val breaker = createBreaker()

        // When & Then
        assertThat(breaker.currentState).isEqualTo(CircuitState.CLOSED)
        assertThat(breaker.execute { "OK" }).isEqualTo("OK")
        assertThat(breaker.currentState).isEqualTo(CircuitState.CLOSED)
    }

    @Test
    fun `should open after five consecutive network failures`() = runTest {
        // Given
        val breaker = createBreaker()

        // When
        repeat(5) {
            assertFailsWith<IOException> {
                breaker.execute<String> {
                    throw IOException("Network failure")
                }
            }
        }

        // Then
        assertThat(breaker.currentState).isEqualTo(CircuitState.OPEN)
    }

    @Test
    fun `should reject calls without running block when circuit is open`() = runTest {
        // Given
        val breaker = createBreaker()
        repeat(5) {
            assertFailsWith<IOException> {
                breaker.execute<String> {
                    throw IOException("Network failure")
                }
            }
        }
        var blockCalled = false

        // When
        assertFailsWith<CallNotPermittedException> {
            breaker.execute {
                blockCalled = true
                "Should not run"
            }
        }

        // Then
        assertFalse(blockCalled)
    }

    @Test
    fun `should close circuit after successful probe when open duration has passed`() = runTest {
        // Given
        val breaker = createBreaker()
        repeat(5) {
            assertFailsWith<IOException> {
                breaker.execute<String> {
                    throw IOException("Network failure")
                }
            }
        }
        nowNanos += Duration.ofSeconds(5).toNanos()

        // When
        val result = breaker.execute { "Recovered" }

        // Then
        assertThat(result).isEqualTo("Recovered")
        assertThat(breaker.currentState).isEqualTo(CircuitState.CLOSED)
    }

    @Test
    fun `should reopen circuit for another five seconds when probe fails`() = runTest {
        // Given
        val breaker = createBreaker()
        repeat(5) {
            assertFailsWith<IOException> {
                breaker.execute<String> {
                    throw IOException("Network failure")
                }
            }
        }
        nowNanos += Duration.ofSeconds(5).toNanos()

        // When
        assertFailsWith<IOException> {
            breaker.execute<String> {
                throw IOException("Probe failed")
            }
        }

        // Then
        assertThat(breaker.currentState).isEqualTo(CircuitState.OPEN)

        assertFailsWith<CallNotPermittedException> {
            breaker.execute { "Too early" }
        }

        nowNanos += Duration.ofSeconds(5).toNanos()

        assertThat(breaker.execute { "Recovered" }).isEqualTo("Recovered")
        assertThat(breaker.currentState).isEqualTo(CircuitState.CLOSED)
    }

    @Test
    fun `should count translated network unavailable exception as network failure`() = runTest {
        val breaker = CircuitBreaker(
            failureThreshold = 5,
            openDuration = Duration.ofSeconds(5),
            clockNanos = { nowNanos }
        )

        repeat(5) {
            assertFailsWith<NetworkUnavailableException> {
                breaker.execute<String> {
                    throw NetworkUnavailableException("Remote network unavailable")
                }
            }
        }

        assertThat(breaker.currentState).isEqualTo(CircuitState.OPEN)
    }

    @Test
    fun `should reopen after any failed half open probe`() = runTest {
        val breaker = createBreaker()
        repeat(5) {
            assertFailsWith<IOException> {
                breaker.execute<String> { throw IOException("Network failure") }
            }
        }
        nowNanos += Duration.ofSeconds(5).toNanos()

        assertFailsWith<IllegalArgumentException> {
            breaker.execute<String> { throw IllegalArgumentException("Probe failed") }
        }

        assertThat(breaker.currentState).isEqualTo(CircuitState.OPEN)
        assertFailsWith<CallNotPermittedException> {
            breaker.execute { "Must still be blocked" }
        }
    }

    @Test
    fun `should reject before running suspending pre call work while open`() = runTest {
        val breaker = createBreaker()
        repeat(5) {
            assertFailsWith<IOException> {
                breaker.execute<String> { throw IOException("Network failure") }
            }
        }
        var preCallWasRun = false

        assertFailsWith<CallNotPermittedException> {
            breaker.execute(
                beforeCall = { preCallWasRun = true }
            ) { "Should not run" }
        }

        assertFalse(preCallWasRun)
    }

    @Test
    fun `should not count non network failure toward threshold`() = runTest {
        // Given
        val breaker = createBreaker()
        repeat(4) {
            assertFailsWith<IOException> {
                breaker.execute<String> {
                    throw IOException("Network failure")
                }
            }
        }

        // When
        assertFailsWith<IllegalArgumentException> {
            breaker.execute<String> {
                throw IllegalArgumentException("Invalid input")
            }
        }

        // Then
        assertThat(breaker.currentState).isEqualTo(CircuitState.CLOSED)

        // The non-network failure resets the consecutive-failure streak.
        assertFailsWith<IOException> {
            breaker.execute<String> {
                throw IOException("Network failure")
            }
        }

        assertThat(breaker.currentState).isEqualTo(CircuitState.CLOSED)
    }
}