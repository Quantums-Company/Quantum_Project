package org.bytebloom.logiroute.presentation.presenter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.bytebloom.logiroute.domain.usecase.packages.GetAllPackagesUseCase
import org.bytebloom.logiroute.domain.usecase.routes.GetAllRoutesUseCase
import org.bytebloom.logiroute.domain.usecase.vehicles.GetAllVehiclesUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetAllWarehousesUseCase
import org.bytebloom.logiroute.presentation.model.LoadStatus
import org.bytebloom.logiroute.presentation.model.PackageUi
import org.bytebloom.logiroute.presentation.model.RouteUi
import org.bytebloom.logiroute.presentation.model.UiError
import org.bytebloom.logiroute.presentation.model.VehicleUi
import org.bytebloom.logiroute.presentation.model.WarehouseUi
import org.bytebloom.logiroute.presentation.presenter.mapper.toUi

data class DashboardState(
    val status: LoadStatus = LoadStatus.IDLE,
    val warehouses: List<WarehouseUi> = emptyList(),
    val routes: List<RouteUi> = emptyList(),
    val vehicles: List<VehicleUi> = emptyList(),
    val packages: List<PackageUi> = emptyList(),
    val error: UiError? = null
)

class DashboardPresenter(
    private val getAllWarehouses: GetAllWarehousesUseCase,
    private val getAllRoutes: GetAllRoutesUseCase,
    private val getAllVehicles: GetAllVehiclesUseCase,
    private val getAllPackages: GetAllPackagesUseCase,
    parentScope: CoroutineScope
) : Presenter<DashboardState>(DashboardState(), parentScope) {

    private var loadJob: Job? = null

    fun load() {
        loadJob?.cancel()
        update { it.copy(status = LoadStatus.LOADING, error = null) }

        loadJob = scope.launch {
            val loaded = guarded(onFailure = ::showFailure) { fetchEverything() } ?: return@launch
            update { loaded }
        }
    }

    // The four fetches are independent, so they run in parallel.
    // If one fails, coroutineScope cancels the others instead of leaking them.
    private suspend fun fetchEverything(): DashboardState = coroutineScope {
        val warehouses = async { getAllWarehouses() }
        val routes = async { getAllRoutes() }
        val vehicles = async { getAllVehicles() }
        val packages = async { getAllPackages() }

        DashboardState(
            status = LoadStatus.READY,
            warehouses = warehouses.await().map { it.toUi() },
            routes = routes.await().map { it.toUi() },
            vehicles = vehicles.await().map { it.toUi() },
            packages = packages.await().map { it.toUi() }
        )
    }

    private fun showFailure(error: UiError) =
        update { it.copy(status = LoadStatus.FAILED, error = error) }
}
