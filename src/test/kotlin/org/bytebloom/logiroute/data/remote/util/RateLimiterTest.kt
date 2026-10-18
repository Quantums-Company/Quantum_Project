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
    fun `should start with a full bucket`() {
        // Given
        val limiter = rateLimiter(capacity = 10)

        // When
        val acquired = List(10) { limiter.tryAcquire() }

        // Then
        assertThat(acquired).containsExactlyElementsIn(List(10) { true })
    }

    @Test
    fun `should reject token immediately when bucket is empty`() {
        // Given
        val limiter = rateLimiter(capacity = 10)
        limiter.drain()

        // When
        val acquired = limiter.tryAcquire()

        // Then
        assertThat(acquired).isFalse()
    }

    @Test
    fun `should refill one token when 200 milliseconds have passed`() {
        // Given
        val limiter = rateLimiter(capacity = 10, refillRatePerSecond = 5)
        limiter.drain()
        fakeNowNanos += 200_000_000L

        // When
        val firstAttempt = limiter.tryAcquire()
        val secondAttempt = limiter.tryAcquire()

        // Then
        assertThat(listOf(firstAttempt, secondAttempt)).containsExactly(true, false).inOrder()
    }

    @Test
    fun `should not refill when less than a whole token has accumulated`() {
        // Given
        val limiter = rateLimiter(capacity = 10, refillRatePerSecond = 5)
        limiter.drain()
        fakeNowNanos += 199_000_000L

        // When
        val acquired = limiter.tryAcquire()

        // Then
        assertThat(acquired).isFalse()
    }

    @Test
    fun `should not refill above bucket capacity when a long time has passed`() {
        // Given
        val limiter = rateLimiter(capacity = 10, refillRatePerSecond = 5)
        limiter.drain()
        fakeNowNanos += 60_000_000_000L

        // When
        val acquiredCount = List(20) { limiter.tryAcquire() }.count { it }

        // Then
        assertThat(acquiredCount).isEqualTo(10)
    }

    @Test
    fun `should wait for next token when acquiring from an empty bucket`() = runTest {
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
        val limiter = rateLimiter(capacity = 10)

        // When
        val results = withContext(Dispatchers.Default) {
            List(1_000) { async { limiter.tryAcquire() } }.awaitAll()
        }

        // Then
        assertThat(results.count { it }).isEqualTo(10)
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
}