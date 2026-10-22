package org.bytebloom.logiroute.presentation.screens

import org.bytebloom.logiroute.domain.model.Route
import org.bytebloom.logiroute.domain.model.Warehouse

/** What the route (road) management screen must be able to show. Implemented by the UI. */
interface RoutesManagementScreen : BaseScreen {

    fun showRoutes(routes: List<Route>)

    /** The warehouses a route can connect, to offer them in a picker. */
    fun showWarehouseChoices(warehouses: List<Warehouse>)

    fun showRoute(route: Route)

    fun showRouteNotFound(id: String)

    fun showRouteCreated(route: Route)

    fun showRouteUpdated(route: Route)

    fun showRouteDeleted(id: String, wasDeleted: Boolean)
}
