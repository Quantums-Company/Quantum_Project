package org.bytebloom.util

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
    var retryCount  = 0

    while (true) {
        try {
            retryCount ++
            val result = block()
            if (retryCount  > 1) {
                Logger.info("Attempt $retryCount  succeeded successfully.")
            }
            return Result.success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: DomainException) {
            val isRetryable = shouldRetry(e)

            if (!isRetryable || retryCount  > maxRetries) {
                Logger.warning("Operation failed permanently on attempt $retryCount . Reason: ${e.message}")
                return Result.failure(e)
            }

            Logger.warning("Attempt $retryCount  failed: ${e.message}. Retrying in ${currentDelay}ms...")

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