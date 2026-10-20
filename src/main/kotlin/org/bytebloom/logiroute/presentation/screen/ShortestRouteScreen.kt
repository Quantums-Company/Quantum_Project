package org.bytebloom.logiroute.presentation.screen

import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.presentation.RoutePlan

/** What the "plan the shortest route" screen must be able to show. Implemented by the UI. */
interface ShortestRouteScreen : BaseScreen {

    fun showWarehouses(warehouses: List<Warehouse>)

    /** Routes that point to a warehouse which does not exist and were therefore ignored. */
    fun showSkippedRoutes(routeIds: List<String>)

    fun showShortestRoute(plan: RoutePlan)

    fun showNoRouteFound(origin: Warehouse, destination: Warehouse)
}
