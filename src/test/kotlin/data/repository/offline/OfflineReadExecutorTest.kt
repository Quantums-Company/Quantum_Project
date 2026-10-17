package data.repository.offline

import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.data.offline.OfflineReadExecutor
import org.bytebloom.logiroute.domain.model.exception.NetworkUnavailableException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds

class OfflineReadExecutorTest {

    private val executor = OfflineReadExecutor(remoteTimeout = 100.milliseconds)

    @Test
    fun `read should return remote data and save snapshot when remote call succeeds`() = runTest {
        var snapshotSaved = false
        val expectedData = listOf("DataFromRemote")

        val result = executor.read(
            remoteCall = { expectedData },
            localCall = { listOf("DataFromLocal") },
            saveSnapshot = { snapshotSaved = true }
        )

        assertEquals(expectedData, result)
        assertTrue(snapshotSaved)
    }

    @Test
    fun `read should fallback to local data when network is unavailable`() = runTest {
        val expectedLocalData = listOf("DataFromLocal")

        val result = executor.read(
            remoteCall = { throw NetworkUnavailableException("No internet connection") },
            localCall = { expectedLocalData },
            saveSnapshot = { }
        )

        assertEquals(expectedLocalData, result)
    }

    @Test
    fun `read should fallback to local data when remote call times out`() = runTest {
        val expectedLocalData = listOf("DataFromLocal")

        val result = executor.read(
            remoteCall = {
                delay(300.milliseconds)
                listOf("DataFromRemote")
            },
            localCall = { expectedLocalData },
            saveSnapshot = { }
        )

        assertEquals(expectedLocalData, result)
    }
}