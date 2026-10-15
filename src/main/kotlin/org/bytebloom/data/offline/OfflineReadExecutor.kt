package org.bytebloom.data.offline

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import org.bytebloom.domain.model.exception.NetworkUnavailableException
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
            withTimeout(remoteTimeout) {
                remoteCall()
            }
        } catch (e: CancellationException) {
            if (e is TimeoutCancellationException) {
                return localCall()
            }
            throw e
        } catch (_: NetworkUnavailableException) {
            return localCall()
        } catch (_: Exception) {
            return localCall()
        }

        try {
            saveSnapshot(remoteResult)
        } catch (_: Exception) {
        }

        return remoteResult
    }
}