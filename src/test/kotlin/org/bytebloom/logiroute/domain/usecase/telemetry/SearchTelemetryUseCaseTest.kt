package org.bytebloom.logiroute.domain.usecase.telemetry

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.model.VehicleTelemetry
import org.bytebloom.logiroute.domain.repository.TelemetrySearchRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchTelemetryUseCaseTest {

    private lateinit var telemetrySearchRepository: RecordingTelemetrySearchRepository
    private lateinit var searchTelemetry: SearchTelemetryUseCase

    @BeforeEach
    fun setUp() {
        telemetrySearchRepository = RecordingTelemetrySearchRepository()
        searchTelemetry = SearchTelemetryUseCase(telemetrySearchRepository)
    }

    @Test
    fun `should search only once for final query when five keystrokes arrive within 200 ms`() = runTest {
        // Given
        val queries = operatorTyping("T", "TR", "TRK", "TRK-", "TRK-001")

        // When
        val results = searchTelemetry(queries).toList()

        // Then
        assertThat(telemetrySearchRepository.startedQueries).containsExactly("TRK-001")
        assertThat(results.map { it.query }).containsExactly("TRK-001")
    }

    @Test
    fun `should start search only after 300 ms of silence when typing stops`() = runTest {
        // Given
        val timedRepository = RecordingTelemetrySearchRepository(clock = { testScheduler.currentTime })
        val timedSearch = SearchTelemetryUseCase(timedRepository)
        val queries = operatorTyping("T", "TR", "TRK", "TRK-", "TRK-001")

        // When
        timedSearch(queries).toList()

        // Then
        assertThat(timedRepository.startTimes).containsExactly(LAST_KEYSTROKE_AT_MS + DEBOUNCE_MS)
    }

    @Test
    fun `should cancel in flight search when a new query stabilizes`() = runTest {
        // Given
        val queries = flow {
            emit("TRK-001")
            delay(DEBOUNCE_MS + 100)
            emit("VAN-010")
        }

        // When
        val results = searchTelemetry(queries).toList()

        // Then
        assertThat(telemetrySearchRepository.startedQueries).containsExactly("TRK-001", "VAN-010").inOrder()
        assertThat(telemetrySearchRepository.cancelledQueries).containsExactly("TRK-001")
        assertThat(telemetrySearchRepository.completedQueries).containsExactly("VAN-010")
        assertThat(results.map { it.query }).containsExactly("VAN-010")
    }

    @Test
    fun `should not search again when stabilized query is unchanged`() = runTest {
        // Given
        val queries = flow {
            emit("TRK-001")
            delay(DEBOUNCE_MS + 100)
            emit("TRK-00")
            emit("TRK-001 ")
        }

        // When
        val results = searchTelemetry(queries).toList()

        // Then
        assertThat(telemetrySearchRepository.startedQueries).containsExactly("TRK-001")
        assertThat(results).hasSize(1)
    }

    @Test
    fun `should skip repository when stabilized query is blank`() = runTest {
        // Given
        val queries = flowOf("   ")

        // When
        val results = searchTelemetry(queries).toList()

        // Then
        assertThat(telemetrySearchRepository.startedQueries).isEmpty()
        assertThat(results.single().matches).isEmpty()
    }

    @Test
    fun `should return telemetry matching query when search completes`() = runTest {
        // Given
        val queries = flowOf("trk")

        // When
        val results = searchTelemetry(queries).toList()

        // Then
        assertThat(results.single().matches.map { it.vehicleId })
            .containsExactly("TRK-001", "TRK-002")
    }

    private fun operatorTyping(vararg keystrokes: String): Flow<String> = flow {
        keystrokes.forEachIndexed { index, text ->
            if (index > 0) delay(KEYSTROKE_GAP_MS)
            emit(text)
        }
        delay(IDLE_AFTER_TYPING_MS)
    }

    private class RecordingTelemetrySearchRepository(
        private val clock: () -> Long = { 0L }
    ) : TelemetrySearchRepository {

        val startedQueries = mutableListOf<String>()
        val startTimes = mutableListOf<Long>()
        val completedQueries = mutableListOf<String>()
        val cancelledQueries = mutableListOf<String>()

        override suspend fun searchByVehicleId(query: String): List<VehicleTelemetry> {
            startedQueries += query
            startTimes += clock()
            try {
                delay(NETWORK_LATENCY_MS)
            } catch (exception: CancellationException) {
                cancelledQueries += query
                throw exception
            }
            completedQueries += query
            return FLEET_SNAPSHOT.filter { it.vehicleId.contains(query, ignoreCase = true) }
        }
    }

    private companion object {
        const val DEBOUNCE_MS = 300L
        const val KEYSTROKE_GAP_MS = 40L
        const val LAST_KEYSTROKE_AT_MS = 4 * KEYSTROKE_GAP_MS
        const val IDLE_AFTER_TYPING_MS = 1_000L
        const val NETWORK_LATENCY_MS = 500L

        val FLEET_SNAPSHOT = listOf(
            VehicleTelemetry("TRK-001", 31.50, 35.00, 42.0, 80.0),
            VehicleTelemetry("TRK-002", 31.51, 35.01, 0.0, 64.0),
            VehicleTelemetry("VAN-010", 31.52, 35.02, 55.0, 91.0)
        )
    }
}