package org.bytebloom.logiroute.data.repository.offline

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.data.offline.OfflineReadExecutor
import org.bytebloom.logiroute.domain.model.exception.NetworkUnavailableException
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds

class OfflineReadExecutorTest {

    private val executor = OfflineReadExecutor(remoteTimeout = 100.milliseconds)

    @Test
    fun `should return remote data and save snapshot when remote call succeeds`() = runTest {
        // Given
        var snapshotSaved = false
        val remoteData = listOf("DataFromRemote")

        // When
        val result = executor.read(
            remoteCall = { remoteData },
            localCall = { listOf("DataFromLocal") },
            saveSnapshot = { snapshotSaved = true }
        )

        // Then
        assertThat(result).isEqualTo(remoteData)
        assertThat(snapshotSaved).isTrue()
    }

    @Test
    fun `should return local data when network is unavailable`() = runTest {
        // Given
        val localData = listOf("DataFromLocal")

        // When
        val result = executor.read(
            remoteCall = { throw NetworkUnavailableException("No internet connection") },
            localCall = { localData },
            saveSnapshot = { }
        )

        // Then
        assertThat(result).isEqualTo(localData)
    }

    @Test
    fun `should return local data when remote call times out`() = runTest {
        // Given
        val localData = listOf("DataFromLocal")

        // When
        val result = executor.read(
            remoteCall = {
                delay(300.milliseconds)
                listOf("DataFromRemote")
            },
            localCall = { localData },
            saveSnapshot = { }
        )

        // Then
        assertThat(result).isEqualTo(localData)
    }
}
