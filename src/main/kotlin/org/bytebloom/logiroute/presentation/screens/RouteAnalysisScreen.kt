package org.bytebloom.logiroute.presentation.screens

import org.bytebloom.logiroute.domain.model.Warehouse

/** What the route analysis screen must be able to show. Implemented by the UI. */
interface RouteAnalysisScreen : BaseScreen {

    fun showWarehouses(warehouses: List<Warehouse>)

    /** The route with the fewest stops (not necessarily the shortest in km). */
    fun showFewestHopsRoute(path: List<Warehouse>)

    fun showNoRouteFound(origin: Warehouse, destination: Warehouse)

    fun showHubLink(origin: Warehouse, destination: Warehouse, isLinked: Boolean)

    /** Shortest distance in km between every pair of warehouses; unreachable pairs are infinite. */
    fun showAllPairsDistances(distances: Map<Warehouse, Map<Warehouse, Double>>)
}
