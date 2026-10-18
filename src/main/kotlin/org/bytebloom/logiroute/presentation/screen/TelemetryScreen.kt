package org.bytebloom.logiroute.presentation.screen

import org.bytebloom.logiroute.domain.model.VehicleTelemetry

interface TelemetryScreen {
    fun showLoadingIndicator(isLoading: Boolean)
    fun showErrorMessage(errorMessage: String)
    fun showTelemetry(readings: List<VehicleTelemetry>)
}
