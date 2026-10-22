package org.bytebloom.logiroute.presentation.presenter

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.routing.WarehouseGraph
import org.bytebloom.logiroute.domain.usecase.routes.GetAllRoutesUseCase
import org.bytebloom.logiroute.domain.usecase.routing.FindOptimalPathUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetAllWarehousesUseCase
import org.bytebloom.logiroute.presentation.RoutingData
import org.bytebloom.logiroute.presentation.RoutingDataLoader
import org.bytebloom.logiroute.presentation.screens.ShortestRouteScreen
import org.bytebloom.logiroute.presentation.RouteLeg
import org.bytebloom.logiroute.presentation.RoutePlan

class ShortestRoutePresenter(
    getAllWarehouses: GetAllWarehousesUseCase,
    getAllRoutes: GetAllRoutesUseCase,
    private val pathFinderFor: (WarehouseGraph) -> FindOptimalPathUseCase,
    view: ShortestRouteScreen,
    parentScope: CoroutineScope,
    private val computeDispatcher: CoroutineDispatcher = Dispatchers.Default
) : BasePresenter<ShortestRouteScreen>(view, parentScope) {

    private sealed interface Outcome {
        class Found(val plan: RoutePlan) : Outcome
        class Unreachable(val origin: Warehouse, val destination: Warehouse) : Outcome
    }

    private val loader = RoutingDataLoader(getAllWarehouses, getAllRoutes)

    @Volatile
    private var routingData: RoutingData? = null

    /** Loads warehouses and routes (in parallel) and builds the graph. */
    fun onViewReady(): Job = perform(task = loader::load) { data ->
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

    private suspend fun findRoute(originId: String, destinationId: String): Outcome {
        val data = checkNotNull(routingData) { NOT_LOADED }
        val origin = data.warehouseById(originId)
        val destination = data.warehouseById(destinationId)

        // Dijkstra is CPU-bound: keep it off the caller's (UI) dispatcher.
        val path = withContext(computeDispatcher)
        { pathFinderFor(data.graph)(origin, destination) }
            ?: return Outcome.Unreachable(origin, destination)

        return Outcome.Found(planOf(path, data.graph))
    }

    private fun planOf(path: List<Warehouse>, graph: WarehouseGraph): RoutePlan {
        val legs = path.zipWithNext { from, to ->
            RouteLeg(from, to, distanceKm = graph.adjacencyMap[from]?.get(to) ?: 0.0)
        }
        return RoutePlan(stops = path, legs = legs, totalDistanceKm = legs.sumOf { it.distanceKm })
    }

    private companion object {
        const val NOT_LOADED = "Routing data has not been loaded yet. Call onViewReady() first."
    }
}
