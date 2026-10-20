package org.bytebloom.logiroute.presentation.presenter

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.exception.ResourceNotFoundException
import org.bytebloom.logiroute.domain.routing.WarehouseGraph
import org.bytebloom.logiroute.domain.routing.WarehouseGraphBuilder
import org.bytebloom.logiroute.domain.usecase.routes.GetAllRoutesUseCase
import org.bytebloom.logiroute.domain.usecase.routing.FindOptimalPathUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetAllWarehousesUseCase
import org.bytebloom.logiroute.presentation.RouteLeg
import org.bytebloom.logiroute.presentation.RoutePlan
import org.bytebloom.logiroute.presentation.screen.ShortestRouteScreen

class ShortestRoutePresenter(
    private val getAllWarehouses: GetAllWarehousesUseCase,
    private val getAllRoutes: GetAllRoutesUseCase,
    private val pathFinderFor: (WarehouseGraph) -> FindOptimalPathUseCase,
    view: ShortestRouteScreen,
    parentScope: CoroutineScope,
    private val computeDispatcher: CoroutineDispatcher = Dispatchers.Default
) : BasePresenter<ShortestRouteScreen>(view, parentScope) {

    private class RoutingData(
        val warehouses: List<Warehouse>,
        val graph: WarehouseGraph,
        val skippedRouteIds: List<String>
    )

    private sealed interface Outcome {
        class Found(val plan: RoutePlan) : Outcome
        class Unreachable(val origin: Warehouse, val destination: Warehouse) : Outcome
    }

    @Volatile
    private var routingData: RoutingData? = null

    /** Loads warehouses and routes (in parallel) and builds the graph. */
    fun onViewReady(): Job = perform(task = ::fetchRoutingData) { data ->
        routingData = data
        view.showWarehouses(data.warehouses)
        if (data.skippedRouteIds.isNotEmpty()) view.showSkippedRoutes(data.skippedRouteIds)
    }

    fun onCalculate(originId: String, destinationId: String): Job =
        perform(task = { findRoute(originId, destinationId) }) { outcome ->
            when (outcome) {
                is Outcome.Found -> view.showShortestRoute(outcome.plan)
                is Outcome.Unreachable -> view.showNoRouteFound(outcome.origin, outcome.destination)
            }
        }

    private suspend fun fetchRoutingData(): RoutingData = coroutineScope {
        val warehousesRequest = async { getAllWarehouses() }
        val routesRequest = async { getAllRoutes() }
        val warehouses = warehousesRequest.await()

        val build = WarehouseGraphBuilder(warehouses, routesRequest.await()).build()
        RoutingData(warehouses, build.graph, build.skippedRouteIds)
    }

    private suspend fun findRoute(originId: String, destinationId: String): Outcome {
        val data = checkNotNull(routingData) { NOT_LOADED }
        val origin = data.warehouseById(originId)
        val destination = data.warehouseById(destinationId)

        // Dijkstra is CPU-bound: keep it off the caller's (UI) dispatcher.
        val path = withContext(computeDispatcher) { pathFinderFor(data.graph)(origin, destination) }
            ?: return Outcome.Unreachable(origin, destination)

        return Outcome.Found(planOf(path, data.graph))
    }

    private fun planOf(path: List<Warehouse>, graph: WarehouseGraph): RoutePlan {
        val legs = path.zipWithNext { from, to ->
            RouteLeg(from, to, distanceKm = graph.adjacencyMap[from]?.get(to) ?: 0.0)
        }
        return RoutePlan(stops = path, legs = legs, totalDistanceKm = legs.sumOf { it.distanceKm })
    }

    private fun RoutingData.warehouseById(id: String): Warehouse =
        warehouses.firstOrNull { it.id.equals(id.trim(), ignoreCase = true) }
            ?: throw ResourceNotFoundException("Warehouse '$id' was not found.")

    private companion object {
        const val NOT_LOADED = "Routing data has not been loaded yet. Call onViewReady() first."
    }
}
