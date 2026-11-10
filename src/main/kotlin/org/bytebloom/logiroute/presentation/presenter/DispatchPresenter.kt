package org.bytebloom.logiroute.presentation.presenter

import kotlinx.coroutines.CoroutineScope
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
import org.bytebloom.logiroute.presentation.dependencies.DispatchDependencies
import org.bytebloom.logiroute.presentation.screens.DispatchScreen
import org.bytebloom.logiroute.domain.model.exception.InvalidConfigurationException

class DispatchPresenter(
    private val dependencies: DispatchDependencies,
    view: DispatchScreen,
    parentScope: CoroutineScope
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
            Triple(pkg, warehouse, dependencies.assignPackageToCargoQueue(warehouse, pkg))
        }
    ) { (pkg, warehouse, assigned) -> view.showPackageAssigned(pkg, warehouse, assigned) }

    fun onAddVehicleToHub(warehouseId: String, vehicleId: String): Job = perform(
        task = {
            val known = loaded()
            dependencies.addVehicleToHub(known.warehouse(warehouseId), known.vehicle(vehicleId))
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
            Dispatched(dependencies.dispatchVehicle(
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
                dependencies.dispatchCargo(order, mode)
            }
        ) { view.showDispatchOutcome(mode, it) }

    fun onOptimizeLoad(vehicleId: String, packageIds: List<String>): Job = perform(
        task = {
            val known = loaded()
            val vehicle = known.vehicle(vehicleId)
            val candidates = packageIds.map { known.pkg(it) }
            // The knapsack table grows with capacity x packages: CPU-bound, so off the UI dispatcher.
            val result = withContext(dependencies.computeDispatcher)
            { dependencies.optimizeCargoLoad(vehicle, candidates) }
            Triple(vehicle, candidates, result)
        }
    ) { (vehicle, candidates, result) -> view.showLoadPlan(vehicle, candidates, result) }

    private suspend fun loadAll(): Data = coroutineScope {
        val warehouses = async { dependencies.getAllWarehouses() }
        val vehicles = async { dependencies.getAllVehicles() }
        val packages = async { dependencies.getAllPackages() }
        Data(warehouses.await(), vehicles.await(), packages.await())
    }

    private fun loaded(): Data =
        data ?: throw InvalidConfigurationException(
            "Dispatch data has not been loaded. Call onViewReady() before performing this action."
        )
}