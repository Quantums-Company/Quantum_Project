package org.bytebloom.logiroute.data.offline

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class OfflineReadExecutor(
    private val remoteTimeout: Duration = DEFAULT_REMOTE_TIMEOUT
) {
    companion object {
        private val DEFAULT_REMOTE_TIMEOUT = 3.seconds
    }

    suspend fun <T> read(
        remoteCall: suspend () -> T,
        localCall: suspend () -> T,
        saveSnapshot: suspend (T) -> Unit
    ): T {
        val remoteResult = try {
            withTimeout(remoteTimeout) { remoteCall() }
        } catch (e: TimeoutCancellationException) {
            return localCall()
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            return localCall()
        }

        try {
            saveSnapshot(remoteResult)
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            // A failed cache write must not hide fresh remote data from the caller.
        }

        return remoteResult
    }
}