package org.bytebloom.logiroute.presentation.presenter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.exception.ResourceNotFoundException
import org.bytebloom.logiroute.domain.model.input.update.VehicleUpdateInput
import org.bytebloom.logiroute.presentation.dependencies.VehiclesManagementDependencies
import org.bytebloom.logiroute.presentation.screens.VehiclesManagementScreen

class VehiclesManagementPresenter(
    private val dependencies: VehiclesManagementDependencies,
    view: VehiclesManagementScreen,
    parentScope: CoroutineScope
) : BasePresenter<VehiclesManagementScreen>(view, parentScope) {

    private class Loaded(val vehicles: List<Vehicle>, val warehouses: List<Warehouse>, val packages: List<Package>)

    @Volatile
    private var warehouses: List<Warehouse> = emptyList()

    @Volatile
    private var packages: List<Package> = emptyList()

    /** Loads vehicles, warehouses and packages in parallel; the last two are offered as pickers. */
    fun onViewReady(): Job = perform(task = ::loadAll) { loaded ->
        warehouses = loaded.warehouses
        packages = loaded.packages
        view.showWarehouseChoices(loaded.warehouses)
        view.showPackageChoices(loaded.packages)
        view.showVehicles(loaded.vehicles)
    }

    fun onFindById(id: String): Job = perform(task = { dependencies.getVehicleById(id) }) { found ->
        if (found == null) view.showVehicleNotFound(id) else view.showVehicle(found)
    }

    fun onCreate(maxCapacityKg: Double, costPerKm: Double, warehouseId: String): Job = perform(
        task = { dependencies.createVehicle(maxCapacityKg, costPerKm, warehouseById(warehouseId)) }
    ) { view.showVehicleCreated(it) }

    /** A null value means "keep the current one". */
    fun onUpdate(id: String, maxCapacityKg: Double?, costPerKm: Double?, warehouseId: String?): Job = perform(
        task = {
            dependencies.updateVehicle(
                VehicleUpdateInput(
                    id = id,
                    maxCapacityKg = maxCapacityKg,
                    costPerKm = costPerKm,
                    currentWarehouse = warehouseId?.let { warehouseById(it) }
                )
            )
        }
    ) { view.showVehicleUpdated(it) }

    fun onDelete(id: String): Job =
        perform(task = { dependencies.deleteVehicle(id) }) { view.showVehicleDeleted(id, it) }

    fun onFindStationed(warehouseId: String, requiredCapacityKg: Double): Job = perform(
        task = {
            val warehouse = warehouseById(warehouseId)
            warehouse to dependencies.findStationedVehiclesByCapacity(warehouse, requiredCapacityKg)
        }
    ) { (warehouse, vehicles) -> view.showStationedVehicles(warehouse, requiredCapacityKg, vehicles) }

    fun onFindCheapest(packageIds: List<String>): Job = perform(
        task = {
            val chosen = packageIds.map { packageById(it) }
            chosen to dependencies.findCheapestSuitableVehicle(chosen)
        }
    ) { (chosen, vehicle) -> view.showCheapestVehicle(chosen, vehicle) }

    private suspend fun loadAll(): Loaded = coroutineScope {
        val vehicles = async { dependencies.getAllVehicles() }
        val warehouses = async { dependencies.getAllWarehouses() }
        val packages = async { dependencies.getAllPackages() }
        Loaded(vehicles.await(), warehouses.await(), packages.await())
    }

    private suspend fun warehouseById(id: String): Warehouse {
        val known = warehouses.ifEmpty { dependencies.getAllWarehouses().also { warehouses = it } }
        return known.firstOrNull { it.id.equals(id.trim(), ignoreCase = true) }
            ?: throw ResourceNotFoundException("Warehouse '$id' was not found.")
    }

    private suspend fun packageById(id: String): Package {
        val known = packages.ifEmpty { dependencies.getAllPackages().also { packages = it } }
        return known.firstOrNull { it.id.equals(id.trim(), ignoreCase = true) }
            ?: throw ResourceNotFoundException("Package '$id' was not found.")
    }
}