package org.bytebloom.logiroute.data.repository.offline

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.data.offline.OfflineReadExecutor
import org.bytebloom.logiroute.domain.model.exception.NetworkUnavailableException
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.milliseconds
import kotlin.test.assertEquals

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
    fun `should propagate unexpected remote errors instead of hiding them with local data`() = runTest {
        val failure = IllegalStateException("Unexpected remote failure")

        val thrown = assertFailsWith<IllegalStateException> {
            executor.read(
                remoteCall = { throw failure },
                localCall = { listOf("StaleLocalData") },
                saveSnapshot = { }
            )
        }

        assertThat(thrown).hasMessageThat()
            .isEqualTo("Unexpected remote failure")
    }

    @Test
    fun `should propagate coroutine cancellation instead of falling back to local data`() = runTest {
        val cancellation = CancellationException("Caller cancelled")

        val thrown = assertFailsWith<CancellationException> {
            executor.read(
                remoteCall = { throw cancellation },
                localCall = { listOf("LocalData") },
                saveSnapshot = { }
            )
        }

        assertEquals("Caller cancelled", thrown.message)
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
