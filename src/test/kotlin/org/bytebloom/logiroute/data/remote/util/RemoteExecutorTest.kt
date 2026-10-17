package org.bytebloom.logiroute.data.remote.util

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.data.remote.client.SupabaseErrorTranslator
import org.bytebloom.logiroute.data.remote.resilience.CircuitBreaker
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RemoteExecutorTest {

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
                    testScheduler.currentTime * 1_000_000L
                }
            ),
            circuitBreaker = CircuitBreaker()
        )
    }
}