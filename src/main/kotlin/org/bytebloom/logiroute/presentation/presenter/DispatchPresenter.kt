package org.bytebloom.logiroute.presentation.presenter

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.bytebloom.logiroute.domain.dispatch.DispatchMode
import org.bytebloom.logiroute.domain.dispatch.DispatchOrder
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.exception.ResourceNotFoundException
import org.bytebloom.logiroute.domain.usecase.dispatch.AddVehicleToHubUseCase
import org.bytebloom.logiroute.domain.usecase.dispatch.AssignPackageToCargoQueueUseCase
import org.bytebloom.logiroute.domain.usecase.dispatch.DispatchCargoUseCase
import org.bytebloom.logiroute.domain.usecase.dispatch.DispatchVehicleUseCase
import org.bytebloom.logiroute.domain.usecase.dispatch.OptimizeCargoLoadUseCase
import org.bytebloom.logiroute.domain.usecase.packages.GetAllPackagesUseCase
import org.bytebloom.logiroute.domain.usecase.vehicles.GetAllVehiclesUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetAllWarehousesUseCase
import org.bytebloom.logiroute.presentation.screens.DispatchScreen

class DispatchPresenter(
    private val getAllWarehouses: GetAllWarehousesUseCase,
    private val getAllVehicles: GetAllVehiclesUseCase,
    private val getAllPackages: GetAllPackagesUseCase,
    private val assignPackageToCargoQueue: AssignPackageToCargoQueueUseCase,
    private val addVehicleToHub: AddVehicleToHubUseCase,
    private val dispatchVehicle: DispatchVehicleUseCase,
    private val dispatchCargo: DispatchCargoUseCase,
    private val optimizeCargoLoad: OptimizeCargoLoadUseCase,
    view: DispatchScreen,
    parentScope: CoroutineScope,
    private val computeDispatcher: CoroutineDispatcher = Dispatchers.Default
) : BasePresenter<DispatchScreen>(view, parentScope) {

    private class Data(
        val warehouses: List<Warehouse>,
        val vehicles: List<Vehicle>,
        val packages: List<Package>
    ) {
        fun warehouse(id: String): Warehouse = warehouses.firstOrNull { it.id.matches(id) }
            ?: throw ResourceNotFoundException("Warehouse '$id' was not found.")

        fun vehicle(id: String): Vehicle = vehicles.firstOrNull { it.id.matches(id) }
            ?: throw ResourceNotFoundException("Vehicle '$id' was not found.")

        fun pkg(id: String): Package = packages.firstOrNull { it.id.matches(id) }
            ?: throw ResourceNotFoundException("Package '$id' was not found.")

        private fun String.matches(other: String) = equals(other.trim(), ignoreCase = true)
    }

    private class Dispatched(
        val wasDispatched: Boolean,
        val vehicle: Vehicle,
        val packages: List<Package>
    )

    @Volatile
    private var data: Data? = null

    /** Loads warehouses, vehicles and packages in parallel. Call it again to refresh after a change. */
    fun onViewReady(): Job = perform(task = ::loadAll) { loaded ->
        data = loaded
        view.showDispatchData(loaded.warehouses, loaded.vehicles, loaded.packages)
    }

    fun onAssignPackage(warehouseId: String, packageId: String): Job = perform(
        task = {
            val known = loaded()
            val warehouse = known.warehouse(warehouseId)
            val pkg = known.pkg(packageId)
            Triple(pkg, warehouse, assignPackageToCargoQueue(warehouse, pkg))
        }
    ) { (pkg, warehouse, assigned) -> view.showPackageAssigned(pkg, warehouse, assigned) }

    fun onAddVehicleToHub(warehouseId: String, vehicleId: String): Job = perform(
        task = {
            val known = loaded()
            addVehicleToHub(known.warehouse(warehouseId), known.vehicle(vehicleId))
        }
    ) { view.showVehicleStationed(it) }

    fun onDispatchVehicle(
        warehouseId: String,
        vehicleId: String,
        packageIds: List<String>
    ): Job = perform(
        task = {
            val known = loaded()
            val vehicle = known.vehicle(vehicleId)
            val packages = packageIds.map { known.pkg(it) }
            Dispatched(dispatchVehicle(
                packages,
                vehicle,
                known.warehouse(warehouseId)
            ), vehicle, packages)
        }
    ) { view.showVehicleDispatched(it.wasDispatched, it.vehicle, it.packages) }

    fun onDispatchCargo(
        warehouseId: String,
        vehicleId: String,
        packageIds: List<String>,
        mode: DispatchMode
    ): Job =
        perform(
            task = {
                val known = loaded()
                val order = DispatchOrder(
                    packages = packageIds.map { known.pkg(it) },
                    vehicle = known.vehicle(vehicleId),
                    warehouse = known.warehouse(warehouseId)
                )
                dispatchCargo(order, mode)
            }
        ) { view.showDispatchOutcome(mode, it) }

    fun onOptimizeLoad(vehicleId: String, packageIds: List<String>): Job = perform(
        task = {
            val known = loaded()
            val vehicle = known.vehicle(vehicleId)
            val candidates = packageIds.map { known.pkg(it) }
            // The knapsack table grows with capacity x packages: CPU-bound, so off the UI dispatcher.
            val result = withContext(computeDispatcher)
            { optimizeCargoLoad(vehicle, candidates) }
            Triple(vehicle, candidates, result)
        }
    ) { (vehicle, candidates, result) -> view.showLoadPlan(vehicle, candidates, result) }

    private suspend fun loadAll(): Data = coroutineScope {
        val warehouses = async { getAllWarehouses() }
        val vehicles = async { getAllVehicles() }
        val packages = async { getAllPackages() }
        Data(warehouses.await(), vehicles.await(), packages.await())
    }

    private fun loaded(): Data = checkNotNull(data) { NOT_LOADED }

    private companion object {
        const val NOT_LOADED = "Dispatch data has not been loaded yet. Call onViewReady() first."
    }
}
