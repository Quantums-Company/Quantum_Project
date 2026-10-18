package org.bytebloom.logiroute.presentation.screen

import org.bytebloom.logiroute.domain.model.Warehouse

interface RoutesManagementScreen {
    fun showLoadingIndicator(isLoading: Boolean)
    fun showErrorMessage(errorMessage: String)
    fun showWarehouses(warehouses: List<Warehouse>)
    fun showRoute(origin: Warehouse, destination: Warehouse, path: List<Warehouse>?, totalDistanceKm: Double)
    fun showSkippedRoutes(routeIds: List<String>)
}
