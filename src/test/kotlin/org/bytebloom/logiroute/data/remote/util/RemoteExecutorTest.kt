package org.bytebloom.logiroute.data.remote.util

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.data.remote.client.SupabaseErrorTranslator
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RemoteExecutorTest {

    @Test
    fun `should return result of remote call when a token is available`() = runTest {
        // Given
        val executor = RemoteExecutor(
            errorTranslator = SupabaseErrorTranslator(),
            rateLimiter = RateLimiter(nanoClock = { testScheduler.currentTime * 1_000_000L })
        )

        // When
        val result = executor.execute("load", StandardTestDispatcher(testScheduler)) { "data" }

        // Then
        assertThat(result).isEqualTo("data")
    }

    @Test
    fun `should wait for a token before calling remote when bucket is empty`() = runTest {
        // Given
        val executor = RemoteExecutor(
            errorTranslator = SupabaseErrorTranslator(),
            rateLimiter = RateLimiter(
                maxBucketCapacity = 1,
                refillRatePerSecond = 5,
                nanoClock = { testScheduler.currentTime * 1_000_000L }
            )
        )
        val dispatcher = StandardTestDispatcher(testScheduler)
        executor.execute("first call", dispatcher) { Unit }

        // When
        executor.execute("second call", dispatcher) { Unit }

        // Then
        assertThat(currentTime).isEqualTo(200L)
    }
}