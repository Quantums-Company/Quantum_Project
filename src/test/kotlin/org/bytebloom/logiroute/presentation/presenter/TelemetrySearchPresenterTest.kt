package org.bytebloom.logiroute.presentation.presenter

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.model.VehicleTelemetry
import org.bytebloom.logiroute.domain.model.result.TelemetrySearchResult
import org.bytebloom.logiroute.domain.repository.TelemetrySearchRepository
import org.bytebloom.logiroute.domain.usecase.telemetry.SearchTelemetryUseCase
import org.bytebloom.logiroute.presentation.SearchTraceSummary
import org.bytebloom.logiroute.presentation.screens.TelemetrySearchScreen
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TelemetrySearchPresenterTest {

    private lateinit var screen: RecordingTelemetrySearchScreen

    @BeforeEach
    fun setUp() {
        screen = RecordingTelemetrySearchScreen()
    }

    @Test
    fun `should log a single search for five rapid keystrokes and cancel it when a new query arrives`() = runTest {
        // Given
        val presenter = TelemetrySearchPresenter(
            telemetrySearchRepository = SlowTelemetrySearchRepository(),
            searchTelemetryFor = { repository -> SearchTelemetryUseCase(repository) },
            view = screen,
            parentScope = backgroundScope,
            timeSource = testScheduler.timeSource
        )

        // When
        presenter.onRunDemo().join()

        // Then
        assertThat(screen.events).containsExactly(
            "key T @0",
            "key TR @40",
            "key TRK @80",
            "key TRK- @120",
            "key TRK-001 @160",
            "search TRK-001 @460",
            "key VAN-010 @510",
            "cancel TRK-001 @810",
            "search VAN-010 @810",
            "result VAN-010 @1310"
        ).inOrder()
    }

    @Test
    fun `should report summary with one cancelled and one completed search`() = runTest {
        // Given
        val presenter = TelemetrySearchPresenter(
            telemetrySearchRepository = SlowTelemetrySearchRepository(),
            searchTelemetryFor = { repository -> SearchTelemetryUseCase(repository) },
            view = screen,
            parentScope = backgroundScope,
            timeSource = testScheduler.timeSource
        )

        // When
        presenter.onRunDemo().join()

        // Then
        assertThat(screen.summary).isEqualTo(
            SearchTraceSummary(keystrokes = 6, searchesStarted = 2, searchesCancelled = 1, searchesCompleted = 1)
        )
    }

    private class SlowTelemetrySearchRepository : TelemetrySearchRepository {
        override suspend fun searchByVehicleId(query: String): List<VehicleTelemetry> {
            delay(NETWORK_LATENCY_MS)
            return FLEET_SNAPSHOT.filter { it.vehicleId.contains(query, ignoreCase = true) }
        }
    }

    private class RecordingTelemetrySearchScreen : TelemetrySearchScreen {
        val events = mutableListOf<String>()
        var summary: SearchTraceSummary? = null

        override fun showKeystroke(query: String, elapsedMillis: Long) {
            events += "key $query @$elapsedMillis"
        }

        override fun showSearchStarted(query: String, elapsedMillis: Long) {
            events += "search $query @$elapsedMillis"
        }

        override fun showSearchCancelled(query: String, elapsedMillis: Long) {
            events += "cancel $query @$elapsedMillis"
        }

        override fun showSearchResult(result: TelemetrySearchResult, elapsedMillis: Long) {
            events += "result ${result.query} @$elapsedMillis"
        }

        override fun showSummary(summary: SearchTraceSummary) {
            this.summary = summary
        }

        override fun showLoadingIndicator(isLoading: Boolean) = Unit

        override fun showErrorMessage(errorMessage: String) {
            events += "error $errorMessage"
        }
    }

    private companion object {
        const val NETWORK_LATENCY_MS = 500L

        val FLEET_SNAPSHOT = listOf(
            VehicleTelemetry("TRK-001", 31.50, 35.00, 42.0, 80.0),
            VehicleTelemetry("VAN-010", 31.52, 35.02, 55.0, 91.0)
        )
    }
}