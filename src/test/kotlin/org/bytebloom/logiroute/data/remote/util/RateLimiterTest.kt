package org.bytebloom.logiroute.data.remote.util

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class RateLimiterTest {

    private var fakeNowNanos = 0L

    @Test
    fun `should start with a full bucket`() {
        // Given
        val limiter = rateLimiter(capacity = DEFAULT_CAPACITY)

        // When
        val acquired = List(DEFAULT_CAPACITY) { limiter.tryAcquire() }

        // Then
        assertThat(acquired).containsExactlyElementsIn(List(DEFAULT_CAPACITY) { true })
    }

    @Test
    fun `should reject token immediately when bucket is empty`() {
        // Given
        val limiter = rateLimiter(capacity = DEFAULT_CAPACITY)
        limiter.drain()

        // When
        val acquired = limiter.tryAcquire()

        // Then
        assertThat(acquired).isFalse()
    }

    @Test
    fun `should refill one token when 200 milliseconds have passed`() {
        // Given
        val limiter = rateLimiter(capacity = DEFAULT_CAPACITY, refillRatePerSecond = DEFAULT_REFILL_RATE)
        limiter.drain()
        fakeNowNanos += TWO_HUNDRED_MS_NANOS

        // When
        val attempts = List(2) { limiter.tryAcquire() }

        // Then
        assertThat(attempts).containsExactly(true, false).inOrder()
    }

    @Test
    fun `should preserve fractional refill when a token is consumed`() {
        // Given
        val limiter = rateLimiter(capacity = 2, refillRatePerSecond = DEFAULT_REFILL_RATE)
        assertThat(limiter.tryAcquire()).isTrue()
        fakeNowNanos += HUNDRED_MS_NANOS

        // When
        val attempts = listOf(limiter.tryAcquire(), limiter.tryAcquire())

        // Then
        assertThat(attempts).containsExactly(true, false).inOrder()
        fakeNowNanos += HUNDRED_MS_NANOS
        assertThat(limiter.tryAcquire()).isTrue()
    }

    @Test
    fun `should not refill when less than a whole token has accumulated`() {
        // Given
        val limiter = rateLimiter(capacity = DEFAULT_CAPACITY, refillRatePerSecond = DEFAULT_REFILL_RATE)
        limiter.drain()
        fakeNowNanos += ONE_HUNDRED_NINETY_NINE_MS_NANOS

        // When
        val acquired = limiter.tryAcquire()

        // Then
        assertThat(acquired).isFalse()
    }

    @Test
    fun `should not refill above bucket capacity when a long time has passed`() {
        // Given
        val limiter = rateLimiter(capacity = DEFAULT_CAPACITY, refillRatePerSecond = DEFAULT_REFILL_RATE)
        limiter.drain()
        fakeNowNanos += SIXTY_SECONDS_NANOS

        // When
        val acquiredCount = List(20) { limiter.tryAcquire() }.count { it }

        // Then
        assertThat(acquiredCount).isEqualTo(DEFAULT_CAPACITY)
    }

    @Test
    fun `should wait for next token when acquiring from an empty bucket`() = runTest {
        // Given
        val limiter = RateLimiter(
            maxBucketCapacity = 1,
            refillRatePerSecond = DEFAULT_REFILL_RATE,
            nanoClock = { testScheduler.currentTime * 1_000_000L }
        )
        limiter.acquire()

        // When
        limiter.acquire()

        // Then
        assertThat(currentTime).isEqualTo(200L)
    }

    @Test
    fun `should acquire without waiting when a token is available`() = runTest {
        // Given
        val limiter = RateLimiter(nanoClock = { testScheduler.currentTime * 1_000_000L })

        // When
        limiter.acquire()

        // Then
        assertThat(currentTime).isEqualTo(0L)
    }

    @Test
    fun `should hand out exactly the capacity when requests are concurrent`() = runBlocking {
        // Given
        val limiter = rateLimiter(capacity = DEFAULT_CAPACITY)

        // When: using real Dispatchers.Default threads intentionally to test concurrent thread safety without delays
        val results = withContext(Dispatchers.Default) {
            List(1_000) { async { limiter.tryAcquire() } }.awaitAll()
        }

        // Then
        assertThat(results.count { it }).isEqualTo(DEFAULT_CAPACITY)
    }

    @Test
    fun `should throw illegal argument exception when capacity is not positive`() {
        // When
        val action = { rateLimiter(capacity = 0) }

        // Then
        assertFailsWith<IllegalArgumentException> { action() }
    }

    @Test
    fun `should throw illegal argument exception when refill rate is not positive`() {
        // When
        val action = { rateLimiter(refillRatePerSecond = 0) }

        // Then
        assertFailsWith<IllegalArgumentException> { action() }
    }

    private fun rateLimiter(capacity: Int = DEFAULT_CAPACITY, refillRatePerSecond: Int = DEFAULT_REFILL_RATE) =
        RateLimiter(
            maxBucketCapacity = capacity,
            refillRatePerSecond = refillRatePerSecond,
            nanoClock = { fakeNowNanos }
        )

    private fun RateLimiter.drain() {
        do {
            val acquired = tryAcquire()
        } while (acquired)
    }

    companion object {
        private const val DEFAULT_CAPACITY = 10
        private const val DEFAULT_REFILL_RATE = 5
        private const val HUNDRED_MS_NANOS = 100_000_000L
        private const val TWO_HUNDRED_MS_NANOS = 200_000_000L
        private const val ONE_HUNDRED_NINETY_NINE_MS_NANOS = 199_000_000L
        private const val SIXTY_SECONDS_NANOS = 60_000_000_000L
    }
}