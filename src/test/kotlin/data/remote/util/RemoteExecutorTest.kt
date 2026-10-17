package data.remote.util

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.data.remote.client.SupabaseErrorTranslator
import org.bytebloom.logiroute.data.remote.util.RateLimiter
import org.bytebloom.logiroute.data.remote.util.RemoteExecutor
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class RemoteExecutorTest {

    @Test
    fun `returns the result of the remote call`() = runTest {
        // Given
        val executor = RemoteExecutor(
            errorTranslator = SupabaseErrorTranslator(),
            rateLimiter = RateLimiter(nanoClock = { testScheduler.currentTime * 1_000_000L })
        )

        // When
        val result = executor.execute("load", StandardTestDispatcher(testScheduler)) { "data" }

        // Then
        assertEquals("data", result)
    }

    @Test
    fun `waits for a token before calling remote when the bucket is empty`() = runTest {
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
        assertEquals(200L, currentTime)
    }
}