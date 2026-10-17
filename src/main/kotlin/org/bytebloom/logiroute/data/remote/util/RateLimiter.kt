package org.bytebloom.logiroute.data.remote.util

import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.ceil
import kotlin.math.min
import kotlin.time.Duration.Companion.nanoseconds

class RateLimiter(
    private val maxBucketCapacity: Int = DEFAULT_MAX_BUCKET_CAPACITY,
    private val refillRatePerSecond: Int = DEFAULT_REFILL_RATE_PER_SECOND,
    private val nanoClock: () -> Long = System::nanoTime
) {

    private data class BucketState(
        val availableTokens: Double,
        val lastRefillNanos: Long
    )

    init {
        require(maxBucketCapacity > 0) { "Bucket capacity must be positive." }
        require(refillRatePerSecond > 0) { "Refill rate must be positive." }
    }

    private val bucket = AtomicReference(
        BucketState(availableTokens = maxBucketCapacity.toDouble(), lastRefillNanos = nanoClock())
    )

    fun tryAcquire(): Boolean {
        while (true) {
            val current = bucket.get()
            val refilled = current.refilledAt(nanoClock())

            if (refilled.availableTokens < ONE_TOKEN) {
                return false
            }

            val consumed = refilled.copy(availableTokens = refilled.availableTokens - ONE_TOKEN)
            if (bucket.compareAndSet(current, consumed)) {
                return true
            }
        }
    }

    suspend fun acquire() {
        while (!tryAcquire()) {
            delay(nanosUntilNextToken().nanoseconds)
        }
    }

    private fun BucketState.refilledAt(nowNanos: Long): BucketState {
        val elapsedNanos = nowNanos - lastRefillNanos
        if (elapsedNanos <= 0) return this

        val refilledTokens = elapsedNanos * refillRatePerSecond / NANOS_PER_SECOND
        return BucketState(
            availableTokens = min(maxBucketCapacity.toDouble(), availableTokens + refilledTokens),
            lastRefillNanos = nowNanos
        )
    }

    private fun nanosUntilNextToken(): Long {
        val refilled = bucket.get().refilledAt(nanoClock())
        val missingTokens = ONE_TOKEN - refilled.availableTokens
        return ceil(missingTokens * NANOS_PER_SECOND / refillRatePerSecond)
            .toLong()
            .coerceAtLeast(MIN_WAIT_NANOS)
    }

    private companion object {
        const val DEFAULT_MAX_BUCKET_CAPACITY = 10
        const val DEFAULT_REFILL_RATE_PER_SECOND = 5
        const val ONE_TOKEN = 1.0
        const val NANOS_PER_SECOND = 1_000_000_000.0
        const val MIN_WAIT_NANOS = 1L
    }
}