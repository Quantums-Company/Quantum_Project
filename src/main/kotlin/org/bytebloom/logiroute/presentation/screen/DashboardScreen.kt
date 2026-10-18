package org.bytebloom.logiroute.presentation.screen

interface DashboardScreen {
    fun showLoadingIndicator(isLoading: Boolean)
    fun showErrorMessage(errorMessage: String)
    fun showDashboard(warehouseCount: Int, routeCount: Int, packageCount: Int, vehicleCount: Int)
}
