package org.bytebloom.data.remote.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bytebloom.data.remote.client.SupabaseErrorTranslator

class RemoteExecutor(
    private val errorTranslator: SupabaseErrorTranslator
) {
    suspend fun <T> execute(
        operation: String,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        block: suspend () -> T
    ): T = withContext(dispatcher) {
        retryWithBackoff {
            errorTranslator.translate(operation, block)
        }.getOrThrow()
    }
}