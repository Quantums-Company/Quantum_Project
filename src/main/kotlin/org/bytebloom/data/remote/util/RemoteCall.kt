package org.bytebloom.data.remote.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bytebloom.data.remote.client.SupabaseErrorTranslator

/*
  Executes a remote operation on the I/O dispatcher
  Translates remote errors and preserves cancellation
 */
suspend fun <T> remoteCall(operation: String, dispatcher: CoroutineDispatcher = Dispatchers.IO, block: suspend () -> T):
        T = withContext(dispatcher) { retryWithBackoff { SupabaseErrorTranslator.translate(operation, block) }.getOrThrow() }