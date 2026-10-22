package org.bytebloom.logiroute.presentation.presenter

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.bytebloom.logiroute.domain.model.VehicleTelemetry
import org.bytebloom.logiroute.domain.usecase.telemetry.StreamFleetTelemetryUseCase
import org.bytebloom.logiroute.domain.usecase.telemetry.TrackActiveFleetUseCase
import org.bytebloom.logiroute.domain.usecase.vehicles.GetAllVehiclesUseCase
import org.bytebloom.logiroute.presentation.toUserMessage
import org.bytebloom.logiroute.presentation.screens.TelemetryScreen

class TelemetryPresenter(
    private val getAllVehicles: GetAllVehiclesUseCase,
    private val streamFleetTelemetry: StreamFleetTelemetryUseCase,
    private val trackActiveFleet: TrackActiveFleetUseCase,
    view: TelemetryScreen,
    parentScope: CoroutineScope
) : BasePresenter<TelemetryScreen>(view, parentScope) {

    @Volatile
    private var streaming: Job? = null

    /** Starts the stream. Calling it again while a stream is running returns the running one. */
    fun onStart(): Job {
        streaming?.takeIf { it.isActive }?.let { return it }
        return scope.launch { stream() }.also { streaming = it }
    }

    /** Cancels the stream. The data source stops producing at once because the collector is gone. */
    fun onStop() {
        streaming?.cancel()
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun stream() {
        val latestByVehicle = LinkedHashMap<String, VehicleTelemetry>()
        var started = false

        try {
            view.showLoadingIndicator(true)
            val vehicleIds = getAllVehicles().map { it.id }.toSet()
            view.showLoadingIndicator(false)

            if (vehicleIds.isEmpty()) {
                view.showErrorMessage(NO_VEHICLES)
                return
            }

            started = true
            view.showTrackingStarted(vehicleIds)

            // Two independent cold flows, collected side by side.
            // If either fails, coroutineScope cancels the other: nothing keeps running in the background.
            coroutineScope {
                launch {
                    streamFleetTelemetry(vehicleIds).collect { telemetry ->
                        latestByVehicle[telemetry.vehicleId] = telemetry
                        view.showTelemetry(telemetry)
                    }
                }
                launch {
                    trackActiveFleet(vehicleIds).collect(view::showDelayAlert)
                }
            }
        } catch (cancellation: CancellationException) {
            view.showLoadingIndicator(false)
            throw cancellation
        } catch (failure: Exception) {
            view.showLoadingIndicator(false)
            view.showErrorMessage(failure.toUserMessage())
        } finally {
            if (started) view.showTrackingStopped(latestByVehicle.values.toList())
        }
    }

    private companion object {
        const val NO_VEHICLES = "No vehicles are available to track."
    }
}
