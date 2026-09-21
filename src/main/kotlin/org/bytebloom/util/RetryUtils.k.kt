package org.bytebloom.util

import kotlinx.coroutines.delay
import org.bytebloom.domain.exception.EntityValidationException

suspend fun <T> retryWithBackoff(
    maxRetries: Int = 3,
    initialDelayMs: Long = 1000,
    factor: Double = 2.0,
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
        } catch (e: Throwable) {
            val errorMessage = e.message ?: ""
            val isNonRetryable = e is EntityValidationException ||
                    errorMessage.contains("401") ||
                    errorMessage.contains("422") ||
                    errorMessage.contains("Unauthorized")

            if (isNonRetryable || attempt >= maxRetries) {
                Logger.warning("Operation failed permanently on attempt $attempt. Reason: ${e.message}")
                return Result.failure(e)
            }

            Logger.warning("Attempt $attempt failed: ${e.message}. Retrying in ${currentDelay}ms...")

            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong()
        }
    }
}