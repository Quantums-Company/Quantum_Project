package org.bytebloom.logiroute.data.remote.util

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import org.bytebloom.logiroute.domain.model.exception.DomainException
import org.bytebloom.logiroute.domain.model.exception.NetworkUnavailableException

suspend fun <T> retryWithBackoff(
    maxRetries: Int = 3,
    initialDelayMs: Long = 1000,
    backoffMultiplier: Double = 2.0,
    shouldRetry: (Exception) -> Boolean = { isRetryableNetworkException(it) },
    block: suspend () -> T
): Result<T> {
    var retryDelayMs = initialDelayMs
    var attemptCount = 0

    while (true) {
        try {
            attemptCount++
            return Result.success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: DomainException) {
            val isRetryable = shouldRetry(e)
            if (!isRetryable || attemptCount > maxRetries) {
                return Result.failure(e)
            }
            delay(retryDelayMs.milliseconds)
            retryDelayMs = (retryDelayMs * backoffMultiplier).toLong()
        }
    }
}

private fun isRetryableNetworkException(e: Exception): Boolean {
    return e is NetworkUnavailableException
}