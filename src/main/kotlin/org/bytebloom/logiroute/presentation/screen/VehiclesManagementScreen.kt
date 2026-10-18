package org.bytebloom.logiroute.presentation.screen

import org.bytebloom.logiroute.domain.model.Vehicle

interface VehiclesManagementScreen {
    fun showLoadingIndicator(isLoading: Boolean)
    fun showErrorMessage(errorMessage: String)
    fun showVehicles(vehicles: List<Vehicle>)
}
