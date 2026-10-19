package org.bytebloom.logiroute.data.remote.util

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.data.remote.client.SupabaseErrorTranslator
import org.bytebloom.logiroute.data.remote.resilience.CircuitBreaker
import org.bytebloom.logiroute.data.remote.exception.CallNotPermittedException
import org.bytebloom.logiroute.domain.model.exception.NetworkUnavailableException
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class RemoteExecutorTest {

    private companion object {
        const val FAILURES_TO_OPEN_CIRCUIT = 5
        const val NANOS_PER_MILLI = 1_000_000L
    }

    @Test
    fun `should return the result of the remote call`() = runTest {
        // Given
        val executor = createRemoteExecutor(testScheduler = testScheduler)
        val dispatcher = StandardTestDispatcher(testScheduler)

        // When
        val result = executor.execute("load", dispatcher) {
            "data"
        }

        // Then
        assertThat(result).isEqualTo("data")
    }

    @Test
    fun `should wait for a token before calling remote when the bucket is empty`() = runTest {
        // Given
        val executor = createRemoteExecutor(
            maxBucketCapacity = 1,
            refillRatePerSecond = 5,
            testScheduler = testScheduler
        )
        val dispatcher = StandardTestDispatcher(testScheduler)

        executor.execute("first call", dispatcher) {
        }

        // When
        executor.execute("second call", dispatcher) {
        }

        // Then
        assertThat(currentTime).isEqualTo(200L)
    }

    @Test
    fun `should fail fast when circuit is open even if rate limiter has no token`() = runTest {
        // Given
        val executor = RemoteExecutor(
            errorTranslator = SupabaseErrorTranslator(),
            rateLimiter = createDrainedRateLimiter(testScheduler),
            circuitBreaker = createOpenCircuitBreaker()
        )
        var remoteBlockCalled = false

        // When
        val action = suspend {
            executor.execute("blocked call", StandardTestDispatcher(testScheduler)) {
                remoteBlockCalled = true
                "must not execute"
            }
        }

        // Then
        assertFailsWith<CallNotPermittedException> { action() }
        assertThat(remoteBlockCalled).isFalse()
        assertThat(currentTime).isEqualTo(0L)
    }

    private suspend fun createOpenCircuitBreaker(): CircuitBreaker {
        val breaker = CircuitBreaker(clockNanos = { 0L })
        repeat(FAILURES_TO_OPEN_CIRCUIT) {
            runCatching {
                breaker.execute<String> { throw NetworkUnavailableException("Simulated network outage") }
            }
        }
        return breaker
    }

    private suspend fun createDrainedRateLimiter(testScheduler: TestCoroutineScheduler): RateLimiter =
        RateLimiter(
            maxBucketCapacity = 1,
            refillRatePerSecond = 5,
            nanoClock = { testScheduler.currentTime * NANOS_PER_MILLI }
        ).also { it.acquire() }

    // --- Private Builders ---

    private fun createRemoteExecutor(
        maxBucketCapacity: Int = 10,
        refillRatePerSecond: Int = 10,
        testScheduler: TestCoroutineScheduler
    ): RemoteExecutor {
        return RemoteExecutor(
            errorTranslator = SupabaseErrorTranslator(),
            rateLimiter = RateLimiter(
                maxBucketCapacity = maxBucketCapacity,
                refillRatePerSecond = refillRatePerSecond,
                nanoClock = {
                    testScheduler.currentTime * NANOS_PER_MILLI
                }
            ),
            circuitBreaker = CircuitBreaker()
        )
    }
}