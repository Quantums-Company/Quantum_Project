package org.bytebloom.logiroute.presentation.presenter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.exception.InvalidConfigurationException
import org.bytebloom.logiroute.presentation.RoutingData
import org.bytebloom.logiroute.presentation.RoutingDataLoader
import org.bytebloom.logiroute.presentation.dependencies.RouteAnalysisDependencies
import org.bytebloom.logiroute.presentation.screens.RouteAnalysisScreen

class RouteAnalysisPresenter(
    private val dependencies: RouteAnalysisDependencies,
    view: RouteAnalysisScreen,
    parentScope: CoroutineScope
) : BasePresenter<RouteAnalysisScreen>(view, parentScope) {

    private sealed interface Hops {
        class Found(val path: List<Warehouse>) : Hops
        class Unreachable(val origin: Warehouse, val destination: Warehouse) : Hops
    }

    private class Link(val origin: Warehouse, val destination: Warehouse, val isLinked: Boolean)

    private val loader = RoutingDataLoader(dependencies.getAllWarehouses, dependencies.getAllRoutes)

    @Volatile
    private var routingData: RoutingData? = null

    fun onViewReady(): Job = perform(task = loader::load) { data ->
        routingData = data
        view.showWarehouses(data.warehouses)
    }

    fun onFindFewestHops(originId: String, destinationId: String): Job =
        perform(task = { fewestHops(originId, destinationId) }) { outcome ->
            when (outcome) {
                is Hops.Found -> view.showFewestHopsRoute(outcome.path)
                is Hops.Unreachable -> view.showNoRouteFound(outcome.origin, outcome.destination)
            }
        }

    fun onVerifyLink(originId: String, destinationId: String): Job =
        perform(task = { verifyLink(originId, destinationId) }) { link ->
            view.showHubLink(link.origin, link.destination, link.isLinked)
        }

    fun onShowAllPairs(): Job = perform(task = { allPairs() }) { view.showAllPairsDistances(it) }

    private suspend fun fewestHops(originId: String, destinationId: String): Hops {
        val data = loaded()
        val origin = data.warehouseById(originId)
        val destination = data.warehouseById(destinationId)
        val path = withContext(dependencies.computeDispatcher)
        { dependencies.fewestHopsFor(data.graph)(origin, destination) }
            ?: return Hops.Unreachable(origin, destination)
        return Hops.Found(path)
    }

    private suspend fun verifyLink(originId: String, destinationId: String): Link {
        val data = loaded()
        val origin = data.warehouseById(originId)
        val destination = data.warehouseById(destinationId)
        val linked = withContext(dependencies.computeDispatcher) {
            dependencies.verifyLinkFor(data.graph)(origin, destination)
        }
        return Link(origin, destination, linked)
    }

    private suspend fun allPairs(): Map<Warehouse, Map<Warehouse, Double>> {
        val data = loaded()
        // One Dijkstra run per pair: CPU-bound, so off the caller's (UI) dispatcher.
        return withContext(dependencies.computeDispatcher) { dependencies.allPairsFor(data.graph)() }
    }

    private fun loaded(): RoutingData =
        routingData ?: throw InvalidConfigurationException(
            "Routing data has not been loaded. Call onViewReady() before performing this action."
        )
}