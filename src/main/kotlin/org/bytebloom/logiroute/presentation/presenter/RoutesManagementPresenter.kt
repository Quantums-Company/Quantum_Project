package org.bytebloom.logiroute.presentation.presenter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.bytebloom.logiroute.domain.model.Route
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.exception.ResourceNotFoundException
import org.bytebloom.logiroute.domain.model.input.update.RouteUpdateInput
import org.bytebloom.logiroute.presentation.dependencies.RoutesManagementDependencies
import org.bytebloom.logiroute.presentation.screens.RoutesManagementScreen

class RoutesManagementPresenter(
    private val dependencies: RoutesManagementDependencies,
    view: RoutesManagementScreen,
    parentScope: CoroutineScope
) : BasePresenter<RoutesManagementScreen>(view, parentScope) {

    private class Loaded(val routes: List<Route>, val warehouses: List<Warehouse>)

    @Volatile
    private var warehouses: List<Warehouse> = emptyList()

    /** Loads routes and warehouses in parallel; the warehouses are offered as a picker. */
    fun onViewReady(): Job = perform(task = ::loadAll) { loaded ->
        warehouses = loaded.warehouses
        view.showWarehouseChoices(loaded.warehouses)
        view.showRoutes(loaded.routes)
    }

    fun onFindById(id: String): Job = perform(task = { dependencies.getRouteById(id) }) { found ->
        if (found == null) view.showRouteNotFound(id) else view.showRoute(found)
    }

    fun onCreate(distanceKm: Double, typicalDelayMin: Int, originId: String, destinationId: String): Job = perform(
        task = { dependencies.createRoute(
            distanceKm,
            typicalDelayMin,
            warehouseById(originId),
            warehouseById(destinationId)
        ) }
    ) { view.showRouteCreated(it) }

    /** A null value means "keep the current one". */
    fun onUpdate(
        id: String,
        distanceKm: Double?,
        typicalDelayMin: Int?,
        originId: String?,
        destinationId: String?
    ): Job = perform(
        task = {
            dependencies.updateRoute(
                RouteUpdateInput(
                    id = id,
                    distanceKm = distanceKm,
                    typicalDelayMin = typicalDelayMin,
                    originWarehouse = originId?.let { warehouseById(it) },
                    destinationWarehouse = destinationId?.let { warehouseById(it) }
                )
            )
        }
    ) { view.showRouteUpdated(it) }

    fun onDelete(id: String): Job =
        perform(task = { dependencies.deleteRoute(id) }) { view.showRouteDeleted(id, it) }

    private suspend fun loadAll(): Loaded = coroutineScope {
        val routes = async { dependencies.getAllRoutes() }
        val warehouses = async { dependencies.getAllWarehouses() }
        Loaded(routes.await(), warehouses.await())
    }

    private suspend fun warehouseById(id: String): Warehouse {
        val known = warehouses.ifEmpty { dependencies.getAllWarehouses().also { warehouses = it } }
        return known.firstOrNull { it.id.equals(id.trim(), ignoreCase = true) }
            ?: throw ResourceNotFoundException("Warehouse '$id' was not found.")
    }
}