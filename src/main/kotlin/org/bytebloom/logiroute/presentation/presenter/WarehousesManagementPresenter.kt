package org.bytebloom.logiroute.presentation.presenter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.exception.ResourceNotFoundException
import org.bytebloom.logiroute.domain.model.input.update.WarehouseUpdateInput
import org.bytebloom.logiroute.presentation.dependencies.WarehousesManagementDependencies
import org.bytebloom.logiroute.presentation.screens.WarehousesManagementScreen

class WarehousesManagementPresenter(
    private val dependencies: WarehousesManagementDependencies,
    view: WarehousesManagementScreen,
    parentScope: CoroutineScope
) : BasePresenter<WarehousesManagementScreen>(view, parentScope) {

    fun onViewReady(): Job = perform(task = { dependencies.getAllWarehouses() }) { view.showWarehouses(it) }

    fun onFindById(id: String): Job = perform(task = { dependencies.getWarehouseById(id) }) { found ->
        if (found == null) view.showWarehouseNotFound(id) else view.showWarehouse(found)
    }

    fun onCreate(name: String, regionalZone: String, longitude: Double, latitude: Double): Job = perform(
        task = { dependencies.createWarehouse(name, regionalZone, longitude, latitude) }
    ) { view.showWarehouseCreated(it) }

    fun onUpdate(input: WarehouseUpdateInput): Job =
        perform(task = { dependencies.updateWarehouse(input) }) { view.showWarehouseUpdated(it) }

    fun onDelete(id: String): Job =
        perform(task = { dependencies.deleteWarehouse(id) }) { view.showWarehouseDeleted(id, it) }

    fun onReport(id: String): Job = perform(task = { dependencies.getWarehouseReport(id) }) { report ->
        if (report == null) view.showWarehouseNotFound(id) else view.showReport(report)
    }

    fun onLoadFactor(id: String): Job = perform(task = { dependencies.getWarehouseById(id) }) { found ->
        if (found == null)
            view.showWarehouseNotFound(id)
        else
            view.showLoadFactor(found, dependencies.getWarehouseLoadFactor(found))
    }

    fun onHubLineage(id: String): Job = perform(task = { lineageOf(id) }) { (warehouse, lineage) ->
        view.showHubLineage(warehouse, lineage)
    }

    private suspend fun lineageOf(id: String): Pair<Warehouse, List<Warehouse>> {
        val warehouses = dependencies.getAllWarehouses()
        val target = warehouses.firstOrNull { it.id.equals(id.trim(), ignoreCase = true) }
            ?: throw ResourceNotFoundException("Warehouse '$id' was not found.")
        val tree = dependencies.hubTreeBuilder.build(warehouses)
        return target to dependencies.lineageFor(tree)(target)
    }
}
