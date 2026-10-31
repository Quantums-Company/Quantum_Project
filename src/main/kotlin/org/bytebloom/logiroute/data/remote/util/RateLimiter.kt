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

    private val tokenBucket = AtomicReference(
        BucketState(availableTokens = maxBucketCapacity.toDouble(), lastRefillNanos = nanoClock())
    )

    fun tryAcquire(): Boolean {
        while (true) {
            val currentState = tokenBucket.get()
            val refilledState = currentState.refilledAt(nanoClock())

            if (refilledState.availableTokens < ONE_TOKEN) {
                return false
            }

            val stateAfterTokenAcquisition =
                refilledState.copy(availableTokens = refilledState.availableTokens - ONE_TOKEN)
            if (tokenBucket.compareAndSet(currentState, stateAfterTokenAcquisition)) {
                return true
            }
        }
    }

    suspend fun acquire() {
        while (!tryAcquire()) {
            delay(nanosUntilNextToken().nanoseconds)
        }
    }

    private fun BucketState.refilledAt(currentTimeNanos: Long): BucketState {
        val elapsedTimeNanos = currentTimeNanos - lastRefillNanos
        if (elapsedTimeNanos <= 0) return this

        val newlyRefilledTokens = elapsedTimeNanos.toDouble() * refillRatePerSecond / NANOS_PER_SECOND
        return BucketState(
            availableTokens = min(maxBucketCapacity.toDouble(), availableTokens + newlyRefilledTokens),
            lastRefillNanos = currentTimeNanos
        )
    }

    private fun nanosUntilNextToken(): Long {
        val refilledState = tokenBucket.get().refilledAt(nanoClock())
        val tokensNeeded = ONE_TOKEN - refilledState.availableTokens
        return ceil(tokensNeeded * NANOS_PER_SECOND / refillRatePerSecond)
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