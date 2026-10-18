package org.bytebloom.logiroute.ui.console

import org.bytebloom.logiroute.domain.model.VehicleTelemetry
import org.bytebloom.logiroute.presentation.screen.TelemetryScreen

class ConsoleTelemetryUI : TelemetryScreen {
    override fun showLoadingIndicator(isLoading: Boolean) {
        if (isLoading) println("Collecting 10 simulated telemetry readings; this may take a moment...")
    }

    override fun showErrorMessage(errorMessage: String) = println("[ERROR] $errorMessage")

    override fun showTelemetry(readings: List<VehicleTelemetry>) {
        printSection("FLEET TELEMETRY SAMPLE")
        readings.forEachIndexed { index, reading ->
            println("${index + 1}. ${reading.vehicleId} | speed=${reading.speedKph} km/h | fuel=${reading.fuelPercent}% | lat=${reading.latitude} | lon=${reading.longitude}")
        }
    }
}