package org.bytebloom.util

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import org.bytebloom.domain.model.exception.DomainException
import org.bytebloom.domain.model.exception.EntityValidationException
import kotlin.time.Duration.Companion.milliseconds

suspend fun <T> retryWithBackoff(
    maxRetries: Int = 3,
    initialDelayMs: Long = 1000,
    factor: Double = 2.0,
    shouldRetry: (DomainException) -> Boolean = { defaultRetryPredicate(it) },
    block: suspend () -> T
): Result<T> {
    var currentDelay = initialDelayMs
    var attempt = 0

    while (true) {
        try {
            attempt++
            val result = block()
            if (attempt > 1) {
                Logger.info("Attempt $attempt succeeded successfully.")
            }
            return Result.success(result)
        } catch (e: DomainException) {
            val isRetryable = shouldRetry(e)

            if (!isRetryable || attempt >= maxRetries) {
                Logger.warning("Operation failed permanently on attempt $attempt. Reason: ${e.message}")
                return Result.failure(e)
            }

            Logger.warning("Attempt $attempt failed: ${e.message}. Retrying in ${currentDelay}ms...")

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
    if (e is EntityValidationException) return false

    val errorMessage = e.message.orEmpty()
    val isAuthOrValidationError = errorMessage.contains("401") ||
            errorMessage.contains("422") ||
            errorMessage.contains("Unauthorized")

    return !isAuthOrValidationError
}