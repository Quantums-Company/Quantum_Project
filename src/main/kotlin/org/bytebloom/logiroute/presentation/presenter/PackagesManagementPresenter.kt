package org.bytebloom.logiroute.presentation.presenter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Priority
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.exception.ResourceNotFoundException
import org.bytebloom.logiroute.domain.model.input.update.PackageUpdateInput
import org.bytebloom.logiroute.presentation.dependencies.PackagesManagementDependencies
import org.bytebloom.logiroute.presentation.screens.PackagesManagementScreen

class PackagesManagementPresenter(
    private val dependencies: PackagesManagementDependencies,
    view: PackagesManagementScreen,
    parentScope: CoroutineScope
) : BasePresenter<PackagesManagementScreen>(view, parentScope) {

    private class Loaded(val packages: List<Package>, val warehouses: List<Warehouse>)

    @Volatile
    private var warehouses: List<Warehouse> = emptyList()

    /** Loads packages and warehouses in parallel; the warehouses are offered as a picker. */
    fun onViewReady(): Job = perform(task = ::loadAll) { loaded ->
        warehouses = loaded.warehouses
        view.showWarehouseChoices(loaded.warehouses)
        view.showPackages(loaded.packages)
    }

    fun onFindById(id: String): Job = perform(task = { dependencies.getPackageById(id) }) { found ->
        if (found == null) view.showPackageNotFound(id) else view.showPackage(found)
    }

    fun onCreate(weightKg: Double, priority: Priority, originId: String, destinationId: String): Job = perform(
        task = { dependencies.createPackage(
            weightKg,
            priority,
            warehouseById(originId),
            warehouseById(destinationId)
        ) }
    ) { view.showPackageCreated(it) }

    /** A null value means "keep the current one". */
    fun onUpdate(
        id: String,
        weightKg: Double?,
        priority: Priority?,
        originId: String?,
        destinationId: String?
    ): Job = perform(
        task = {
            dependencies.updatePackage(
                PackageUpdateInput(
                    id = id,
                    weight = weightKg,
                    priority = priority,
                    originWarehouse = originId?.let { warehouseById(it) },
                    destinationWarehouse = destinationId?.let { warehouseById(it) }
                )
            )
        }
    ) { view.showPackageUpdated(it) }

    fun onDelete(id: String): Job =
        perform(task = { dependencies.deletePackage(id) }) { view.showPackageDeleted(id, it) }

    fun onFindAboveWeight(minimumWeightKg: Double): Job = perform(
        task = { dependencies.findPackagesAboveWeight(minimumWeightKg) }
    ) { view.showMatchingPackages("weighing at least $minimumWeightKg kg", it) }

    fun onFindByDestination(destinationId: String): Job = perform(
        task = {
            val destination = warehouseById(destinationId)
            destination to dependencies.findPackagesByDestination(destination)
        }
    ) { (destination, found) ->
        view.showMatchingPackages("going to ${destination.name}", found) }

    fun onFindByPriority(priority: Priority): Job = perform(
        task = { dependencies.findPackagesByPriority(priority) }
    ) { view.showMatchingPackages("with ${priority.name} priority", it) }

    private suspend fun loadAll(): Loaded = coroutineScope {
        val packages = async { dependencies.getAllPackages() }
        val warehouses = async { dependencies.getAllWarehouses() }
        Loaded(packages.await(), warehouses.await())
    }

    private suspend fun warehouseById(id: String): Warehouse {
        val known = warehouses.ifEmpty { dependencies.getAllWarehouses().also { warehouses = it } }
        return known.firstOrNull { it.id.equals(id.trim(), ignoreCase = true) }
            ?: throw ResourceNotFoundException("Warehouse '$id' was not found.")
    }
}