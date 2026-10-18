package org.bytebloom.logiroute.presentation.presenter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.bytebloom.logiroute.domain.usecase.telemetry.StreamFleetTelemetryUseCase
import org.bytebloom.logiroute.domain.usecase.telemetry.TrackActiveFleetUseCase
import org.bytebloom.logiroute.domain.usecase.vehicles.GetAllVehiclesUseCase
import org.bytebloom.logiroute.presentation.model.FleetEventUi
import org.bytebloom.logiroute.presentation.model.UiError
import org.bytebloom.logiroute.presentation.model.VehicleTelemetryUi
import org.bytebloom.logiroute.presentation.presenter.mapper.toUi

enum class TelemetryStatus {
    IDLE,
    STARTING,
    STREAMING,
    STOPPED,
    FAILED
}

data class TelemetryState(
    val status: TelemetryStatus = TelemetryStatus.IDLE,
    val vehicles: List<VehicleTelemetryUi> = emptyList(),
    val events: List<FleetEventUi> = emptyList(),
    val eventCount: Long = 0,
    val error: UiError? = null
)

class TelemetryPresenter(
    private val getAllVehicles: GetAllVehiclesUseCase,
    private val streamFleetTelemetry: StreamFleetTelemetryUseCase,
    private val trackActiveFleet: TrackActiveFleetUseCase,
    parentScope: CoroutineScope,
    private val maxLogEntries: Int = DEFAULT_MAX_LOG_ENTRIES
) : Presenter<TelemetryState>(TelemetryState(), parentScope) {

    private var streamingJob: Job? = null

    init {
        require(maxLogEntries > 0) { "maxLogEntries must be positive." }
    }

    fun start() {
        if (streamingJob?.isActive == true) return

        update { TelemetryState(status = TelemetryStatus.STARTING) }

        streamingJob = scope.launch {
            guarded(onFailure = ::showFailure) { stream() }
        }
    }

    fun stop() {
        streamingJob?.cancel()
        update {
            if (it.status == TelemetryStatus.STARTING || it.status == TelemetryStatus.STREAMING) {
                it.copy(status = TelemetryStatus.STOPPED)
            } else {
                it
            }
        }
    }

    private suspend fun stream() {
        val vehicleIds = getAllVehicles().map { it.id }.toSet()
        if (vehicleIds.isEmpty()) {
            return showFailure(UiError.NotFound("No vehicles are available to track."))
        }

        update { it.copy(status = TelemetryStatus.STREAMING) }

        // Two independent cold flows, collected side by side.
        // If either fails, coroutineScope cancels the other: nothing keeps running in the background.
        coroutineScope {
            launch {
                streamFleetTelemetry(vehicleIds).collect { telemetry -> onReading(telemetry.toUi()) }
            }
            launch {
                trackActiveFleet(vehicleIds).collect { alert -> onDelay(alert.vehicleId, alert.speedKph) }
            }
        }

        update { it.copy(status = TelemetryStatus.STOPPED) }
    }

    private fun onReading(reading: VehicleTelemetryUi) = update { state ->
        val sequence = state.eventCount + 1
        state.copy(
            vehicles = (state.vehicles.filterNot { it.vehicleId == reading.vehicleId } + reading)
                .sortedBy { it.vehicleId },
            events = (state.events + FleetEventUi.Reading(sequence, reading)).takeLast(maxLogEntries),
            eventCount = sequence
        )
    }

    private fun onDelay(vehicleId: String, speedKph: Double) = update { state ->
        val sequence = state.eventCount + 1
        state.copy(
            events = (state.events + FleetEventUi.DelayAlert(sequence, vehicleId, speedKph))
                .takeLast(maxLogEntries),
            eventCount = sequence
        )
    }

    private fun showFailure(error: UiError) =
        update { it.copy(status = TelemetryStatus.FAILED, error = error) }

    private companion object {
        const val DEFAULT_MAX_LOG_ENTRIES = 50
    }
}
