package org.bytebloom.data.remote.util

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import org.bytebloom.domain.model.exception.DomainException
import org.bytebloom.domain.model.exception.NetworkUnavailableException

suspend fun <T> retryWithBackoff(
    maxRetries: Int = 3,
    initialDelayMs: Long = 1000,
    factor: Double = 2.0,
    shouldRetry: (Exception) -> Boolean = { defaultRetryPredicate(it) },
    block: suspend () -> T
): Result<T> {
    var currentDelay = initialDelayMs
    var retryCount = 0

    while (true) {
        try {
            retryCount++
            return Result.success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: DomainException) {
            val isRetryable = shouldRetry(e)
            if (!isRetryable || retryCount > maxRetries) {
                return Result.failure(e)
            }
            delay(currentDelay.milliseconds)
            currentDelay = (currentDelay * factor).toLong()
        }
    }
}

/**
 * Encapsulates default retry rules outside the exception block
 * to keep the handler clean and extensible.
 */
private fun defaultRetryPredicate(e: Exception): Boolean {
    return e is NetworkUnavailableException
}