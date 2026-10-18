package org.bytebloom.logiroute.presentation

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import org.bytebloom.logiroute.domain.usecase.telemetry.StreamFleetTelemetryUseCase
import org.bytebloom.logiroute.domain.usecase.vehicles.GetAllVehiclesUseCase
import org.bytebloom.logiroute.presentation.screen.TelemetryScreen

class TelemetryPresenter(
    private val getAllVehicles: GetAllVehiclesUseCase,
    private val streamFleetTelemetry: StreamFleetTelemetryUseCase,
    private val screen: TelemetryScreen
) {
    suspend fun showSampleTelemetry(sampleCount: Int = 10) {
        require(sampleCount > 0) { "Sample count must be positive." }
        screen.showLoadingIndicator(true)
        try {
            val vehicleIds = getAllVehicles().map { it.id }.toSet()
            if (vehicleIds.isEmpty()) {
                screen.showErrorMessage("No vehicles are available for telemetry.")
                return
            }
            val readings = streamFleetTelemetry(vehicleIds).take(sampleCount).toList()
            screen.showTelemetry(readings)
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            screen.showErrorMessage(error.userMessage())
        } finally {
            screen.showLoadingIndicator(false)
        }
    }
}
