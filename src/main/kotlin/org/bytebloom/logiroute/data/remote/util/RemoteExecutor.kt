package org.bytebloom.logiroute.data.remote.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bytebloom.logiroute.data.remote.client.SupabaseErrorTranslator
import org.bytebloom.logiroute.data.remote.resilience.CircuitBreaker

class RemoteExecutor(
    private val errorTranslator: SupabaseErrorTranslator,
    private val rateLimiter: RateLimiter,
    private val circuitBreaker: CircuitBreaker
) {
    suspend fun <T> execute(
        operation: String,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        block: suspend () -> T
    ): T = withContext(dispatcher) {
        retryWithBackoff {
            circuitBreaker.execute {
                rateLimiter.acquire()
                errorTranslator.translate(operation, block)
            }
        }.getOrThrow()
    }
}