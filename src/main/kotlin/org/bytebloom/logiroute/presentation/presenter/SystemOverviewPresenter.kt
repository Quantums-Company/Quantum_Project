package org.bytebloom.logiroute.presentation.presenter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.bytebloom.logiroute.domain.usecase.packages.GetAllPackagesUseCase
import org.bytebloom.logiroute.domain.usecase.routes.GetAllRoutesUseCase
import org.bytebloom.logiroute.domain.usecase.vehicles.GetAllVehiclesUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetAllWarehousesUseCase
import org.bytebloom.logiroute.presentation.SystemSummary
import org.bytebloom.logiroute.presentation.screens.SystemOverviewScreen

class SystemOverviewPresenter(
    private val getAllWarehouses: GetAllWarehousesUseCase,
    private val getAllRoutes: GetAllRoutesUseCase,
    private val getAllVehicles: GetAllVehiclesUseCase,
    private val getAllPackages: GetAllPackagesUseCase,
    view: SystemOverviewScreen,
    parentScope: CoroutineScope
) : BasePresenter<SystemOverviewScreen>(view, parentScope) {

    /** Loads the four lists in parallel: if one fails the others are cancelled and one message is shown. */
    fun onViewReady(): Job = perform(task = ::loadSummary) { view.showSummary(it) }

    private suspend fun loadSummary(): SystemSummary = coroutineScope {
        val warehouses = async { getAllWarehouses() }
        val routes = async { getAllRoutes() }
        val vehicles = async { getAllVehicles() }
        val packages = async { getAllPackages() }
        SystemSummary(warehouses.await(), routes.await(), vehicles.await(), packages.await())
    }
}
