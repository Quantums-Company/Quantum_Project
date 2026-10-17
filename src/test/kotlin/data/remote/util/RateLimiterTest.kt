package data.remote.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.bytebloom.logiroute.data.remote.util.RateLimiter
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RateLimiterTest {

    private var fakeNowNanos = 0L

    private fun rateLimiter(capacity: Int = 10, refillRatePerSecond: Int = 5) =
        RateLimiter(
            maxBucketCapacity = capacity,
            refillRatePerSecond = refillRatePerSecond,
            nanoClock = { fakeNowNanos }
        )

    private fun RateLimiter.drain() {
        while (tryAcquire()) Unit
    }

    @Test
    fun `starts with a full bucket`() {
        // Given
        val limiter = rateLimiter(capacity = 10)

        // When
        val acquired = List(10) { limiter.tryAcquire() }

        // Then
        assertTrue(acquired.all { it })
    }

    @Test
    fun `tryAcquire returns false immediately when the bucket is empty`() {
        // Given
        val limiter = rateLimiter(capacity = 10)
        limiter.drain()

        // When
        val acquired = limiter.tryAcquire()

        // Then
        assertFalse(acquired)
    }

    @Test
    fun `refills tokens based on elapsed time`() {
        // Given
        val limiter = rateLimiter(capacity = 10, refillRatePerSecond = 5)
        limiter.drain()
        fakeNowNanos += 200_000_000L

        // When
        val firstAttempt = limiter.tryAcquire()
        val secondAttempt = limiter.tryAcquire()

        // Then
        assertEquals(listOf(true, false), listOf(firstAttempt, secondAttempt))
    }

    @Test
    fun `does not refill before a whole token has accumulated`() {
        // Given
        val limiter = rateLimiter(capacity = 10, refillRatePerSecond = 5)
        limiter.drain()
        fakeNowNanos += 199_000_000L

        // When
        val acquired = limiter.tryAcquire()

        // Then
        assertFalse(acquired)
    }

    @Test
    fun `never refills above the bucket capacity`() {
        // Given
        val limiter = rateLimiter(capacity = 10, refillRatePerSecond = 5)
        limiter.drain()
        fakeNowNanos += 60_000_000_000L

        // When
        val acquiredCount = List(20) { limiter.tryAcquire() }.count { it }

        // Then
        assertEquals(10, acquiredCount)
    }

    @Test
    fun `acquire suspends until the next token is refilled`() = runTest {
        // Given
        val limiter = RateLimiter(
            maxBucketCapacity = 1,
            refillRatePerSecond = 5,
            nanoClock = { testScheduler.currentTime * 1_000_000L }
        )
        limiter.acquire()

        // When
        limiter.acquire()

        // Then
        assertEquals(200L, currentTime)
    }

    @Test
    fun `acquire returns without waiting when a token is available`() = runTest {
        // Given
        val limiter = RateLimiter(nanoClock = { testScheduler.currentTime * 1_000_000L })

        // When
        limiter.acquire()

        // Then
        assertEquals(0L, currentTime)
    }

    @Test
    fun `hands out exactly the capacity under concurrent requests`() = runBlocking {
        // Given
        val limiter = rateLimiter(capacity = 10)

        // When
        val results = withContext(Dispatchers.Default) {
            List(1_000) { async { limiter.tryAcquire() } }.awaitAll()
        }

        // Then
        assertEquals(10, results.count { it })
    }

    @Test
    fun `rejects a non positive capacity`() {
        // When
        val result = runCatching { rateLimiter(capacity = 0) }

        // Then
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `rejects a non positive refill rate`() {
        // When
        val result = runCatching { rateLimiter(refillRatePerSecond = 0) }

        // Then
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }
}