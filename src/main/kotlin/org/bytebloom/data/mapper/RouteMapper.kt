package org.bytebloom.data.mapper

import org.bytebloom.data.raw.RouteRaw
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Warehouse

private fun buildRoutes(
    routeRaws: List<RouteRaw>,
    warehouseMap: Map<String, Warehouse>
): List<Route> {
    return routeRaws.mapNotNull { raw ->
        val origin = findWarehouse(
            warehouseMap,
            raw.originWarehouseId,
            ROUTE)
        val destination = findWarehouse(
            warehouseMap,
            raw.destinationWarehouseId,
            ROUTE)

        if (origin == null || destination == null) {
            return@mapNotNull null
        }
        val route = Route(
            raw.id,
            raw.distanceKm,
            raw.typicalDelayMin,
            origin,
            destination).
        also(origin::addRoute)
        route
    }
}