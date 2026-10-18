package org.bytebloom.logiroute.presentation

import kotlinx.coroutines.CancellationException
import org.bytebloom.logiroute.domain.usecase.packages.GetAllPackagesUseCase
import org.bytebloom.logiroute.domain.usecase.routes.GetAllRoutesUseCase
import org.bytebloom.logiroute.domain.usecase.vehicles.GetAllVehiclesUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetAllWarehousesUseCase
import org.bytebloom.logiroute.presentation.screen.DashboardScreen

class DashboardPresenter(
    private val getAllWarehouses: GetAllWarehousesUseCase,
    private val getAllRoutes: GetAllRoutesUseCase,
    private val getAllPackages: GetAllPackagesUseCase,
    private val getAllVehicles: GetAllVehiclesUseCase,
    private val screen: DashboardScreen
) {
    suspend fun showDashboard() {
        screen.showLoadingIndicator(true)
        try {
            val warehouses = getAllWarehouses()
            val routes = getAllRoutes()
            val packages = getAllPackages()
            val vehicles = getAllVehicles()
            screen.showDashboard(warehouses.size, routes.size, packages.size, vehicles.size)
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            screen.showErrorMessage(error.userMessage())
        } finally {
            screen.showLoadingIndicator(false)
        }
    }
}
