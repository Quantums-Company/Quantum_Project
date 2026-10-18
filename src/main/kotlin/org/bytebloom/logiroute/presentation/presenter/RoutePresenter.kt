package org.bytebloom.logiroute.presentation.presenter

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.routing.WarehouseGraph
import org.bytebloom.logiroute.domain.routing.WarehouseGraphBuilder
import org.bytebloom.logiroute.domain.usecase.routes.GetAllRoutesUseCase
import org.bytebloom.logiroute.domain.usecase.routing.FindOptimalPathUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetAllWarehousesUseCase
import org.bytebloom.logiroute.presentation.model.RouteLegUi
import org.bytebloom.logiroute.presentation.model.RoutePlanUi
import org.bytebloom.logiroute.presentation.model.UiError
import org.bytebloom.logiroute.presentation.model.WarehouseUi
import org.bytebloom.logiroute.presentation.presenter.mapper.toUi

enum class RouteStatus {
    IDLE,
    LOADING,
    READY,
    CALCULATING,
    FOUND,
    NO_ROUTE,
    FAILED
}

data class RouteState(
    val status: RouteStatus = RouteStatus.IDLE,
    val warehouses: List<WarehouseUi> = emptyList(),
    val skippedRouteIds: List<String> = emptyList(),
    val plan: RoutePlanUi? = null,
    val error: UiError? = null
)

class RoutePresenter(
    private val getAllWarehouses: GetAllWarehousesUseCase,
    private val getAllRoutes: GetAllRoutesUseCase,
    private val pathFinderFor: (WarehouseGraph) -> FindOptimalPathUseCase,
    parentScope: CoroutineScope,
    private val computeDispatcher: CoroutineDispatcher = Dispatchers.Default
) : Presenter<RouteState>(RouteState(), parentScope) {

    private class RoutingData(
        val warehousesById: Map<String, Warehouse>,
        val graph: WarehouseGraph
    )

    private var routingData: RoutingData? = null
    private var job: Job? = null

    fun load() {
        job?.cancel()
        update { it.copy(status = RouteStatus.LOADING, plan = null, error = null) }

        job = scope.launch {
            val ready = guarded(onFailure = ::showFailure) { fetchRoutingData() } ?: return@launch
            routingData = ready.first
            update { ready.second }
        }
    }

    fun calculate(originId: String, destinationId: String) {
        job?.cancel()
        update { it.copy(status = RouteStatus.CALCULATING, plan = null, error = null) }

        job = scope.launch {
            guarded(onFailure = ::showFailure) { findRoute(originId, destinationId) }
        }
    }

    private suspend fun fetchRoutingData(): Pair<RoutingData, RouteState> = coroutineScope {
        val warehousesRequest = async { getAllWarehouses() }
        val routesRequest = async { getAllRoutes() }
        val warehouses = warehousesRequest.await()

        val build = WarehouseGraphBuilder(warehouses, routesRequest.await()).build()
        val data = RoutingData(warehouses.associateBy { it.id }, build.graph)
        val state = RouteState(
            status = RouteStatus.READY,
            warehouses = warehouses.map { it.toUi() },
            skippedRouteIds = build.skippedRouteIds
        )
        data to state
    }

    private suspend fun findRoute(originId: String, destinationId: String) {
        val data = routingData
            ?: return showFailure(UiError.Unexpected(NOT_LOADED))
        val origin = data.warehouseOrNull(originId)
            ?: return showFailure(UiError.InvalidInput(unknownWarehouse(originId)))
        val destination = data.warehouseOrNull(destinationId)
            ?: return showFailure(UiError.InvalidInput(unknownWarehouse(destinationId)))

        // Dijkstra is CPU-bound: keep it off the caller's (UI) dispatcher.
        val path = withContext(computeDispatcher) { pathFinderFor(data.graph)(origin, destination) }

        if (path == null) {
            update { it.copy(status = RouteStatus.NO_ROUTE, plan = null) }
        } else {
            update { it.copy(status = RouteStatus.FOUND, plan = planOf(path, data.graph)) }
        }
    }

    private fun planOf(path: List<Warehouse>, graph: WarehouseGraph): RoutePlanUi {
        val legs = path.zipWithNext { from, to ->
            RouteLegUi(
                fromName = from.name,
                toName = to.name,
                distanceKm = graph.adjacencyMap[from]?.get(to) ?: 0.0
            )
        }
        return RoutePlanUi(
            stops = path.map { it.toUi() },
            legs = legs,
            totalDistanceKm = legs.sumOf { it.distanceKm }
        )
    }

    private fun RoutingData.warehouseOrNull(id: String): Warehouse? =
        warehousesById.values.firstOrNull { it.id.equals(id.trim(), ignoreCase = true) }

    private fun showFailure(error: UiError) =
        update { it.copy(status = RouteStatus.FAILED, plan = null, error = error) }

    private fun unknownWarehouse(id: String) = "Warehouse '$id' was not found."

    private companion object {
        const val NOT_LOADED = "Routing data has not been loaded yet. Call load() first."
    }
}