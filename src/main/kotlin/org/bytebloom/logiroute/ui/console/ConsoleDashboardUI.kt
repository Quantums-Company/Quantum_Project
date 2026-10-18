package org.bytebloom.logiroute.ui.console

import org.bytebloom.logiroute.presentation.screen.DashboardScreen

class ConsoleDashboardUI : DashboardScreen {
    override fun showLoadingIndicator(isLoading: Boolean) {
        if (isLoading) println("Loading dashboard...")
    }

    override fun showErrorMessage(errorMessage: String) = println("[ERROR] $errorMessage")

    override fun showDashboard(warehouseCount: Int, routeCount: Int, packageCount: Int, vehicleCount: Int) {
        printSection("LOGIROUTE DASHBOARD")
        println("Warehouses : $warehouseCount")
        println("Routes     : $routeCount")
        println("Packages   : $packageCount")
        println("Vehicles   : $vehicleCount")
    }
}