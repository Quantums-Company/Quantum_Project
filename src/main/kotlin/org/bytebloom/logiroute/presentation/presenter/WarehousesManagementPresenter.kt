package org.bytebloom.logiroute.presentation.presenter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.exception.ResourceNotFoundException
import org.bytebloom.logiroute.domain.model.input.update.WarehouseUpdateInput
import org.bytebloom.logiroute.domain.tree.hub.HubTree
import org.bytebloom.logiroute.domain.tree.hub.HubTreeBuilder
import org.bytebloom.logiroute.domain.usecase.warehouses.CreateWarehouseUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.DeleteWarehouseUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetAllWarehousesUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetWarehouseByIdUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetWarehouseLoadFactorUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetWarehouseReportUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.TraceHubLineageUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.UpdateWarehouseUseCase
import org.bytebloom.logiroute.presentation.screens.WarehousesManagementScreen

class WarehousesManagementPresenter(
    private val getAllWarehouses: GetAllWarehousesUseCase,
    private val getWarehouseById: GetWarehouseByIdUseCase,
    private val createWarehouse: CreateWarehouseUseCase,
    private val updateWarehouse: UpdateWarehouseUseCase,
    private val deleteWarehouse: DeleteWarehouseUseCase,
    private val getWarehouseReport: GetWarehouseReportUseCase,
    private val getWarehouseLoadFactor: GetWarehouseLoadFactorUseCase,
    private val hubTreeBuilder: HubTreeBuilder,
    private val lineageFor: (HubTree) -> TraceHubLineageUseCase,
    view: WarehousesManagementScreen,
    parentScope: CoroutineScope
) : BasePresenter<WarehousesManagementScreen>(view, parentScope) {

    fun onViewReady(): Job = perform(task = { getAllWarehouses() }) { view.showWarehouses(it) }

    fun onFindById(id: String): Job = perform(task = { getWarehouseById(id) }) { found ->
        if (found == null) view.showWarehouseNotFound(id) else view.showWarehouse(found)
    }

    fun onCreate(name: String, regionalZone: String, longitude: Double, latitude: Double): Job = perform(
        task = { createWarehouse(name, regionalZone, longitude, latitude) }
    ) { view.showWarehouseCreated(it) }

    fun onUpdate(input: WarehouseUpdateInput): Job =
        perform(task = { updateWarehouse(input) }) { view.showWarehouseUpdated(it) }

    fun onDelete(id: String): Job =
        perform(task = { deleteWarehouse(id) }) { view.showWarehouseDeleted(id, it) }

    fun onReport(id: String): Job = perform(task = { getWarehouseReport(id) }) { report ->
        if (report == null) view.showWarehouseNotFound(id) else view.showReport(report)
    }

    fun onLoadFactor(id: String): Job = perform(task = { getWarehouseById(id) }) { found ->
        if (found == null)
            view.showWarehouseNotFound(id)
        else
            view.showLoadFactor(found, getWarehouseLoadFactor(found))
    }

    fun onHubLineage(id: String): Job = perform(task = { lineageOf(id) }) { (warehouse, lineage) ->
        view.showHubLineage(warehouse, lineage)
    }

    private suspend fun lineageOf(id: String): Pair<Warehouse, List<Warehouse>> {
        val warehouses = getAllWarehouses()
        val target = warehouses.firstOrNull { it.id.equals(id.trim(), ignoreCase = true) }
            ?: throw ResourceNotFoundException("Warehouse '$id' was not found.")
        val tree = hubTreeBuilder.build(warehouses)
        return target to lineageFor(tree)(target)
    }
}
