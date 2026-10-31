package org.bytebloom.logiroute.data.csv.mapper

import org.bytebloom.logiroute.data.csv.raw.RouteRaw
import org.bytebloom.logiroute.domain.model.Route
import org.bytebloom.logiroute.domain.model.Warehouse

class RouteMapper(
    private val warehouseMapper: WarehouseReferenceMapper
) {
    private fun RouteRaw.toDomain(
        originWarehouse: Warehouse,
        destinationWarehouse: Warehouse
    ): Route {
        val route = Route(
            id = id,
            distanceKm = distanceKm,
            typicalDelayMin = typicalDelayMin,
            originWarehouse = originWarehouse,
            destinationWarehouse = destinationWarehouse
        )

        route.originWarehouse.addRoute(route)

        return route
    }

    private fun mapRoute(routeRaw: RouteRaw): Route? {

        val originWarehouse = warehouseMapper.map(
            routeRaw.originWarehouseId
        ) ?: return null

        val destinationWarehouse = warehouseMapper.map(
            routeRaw.destinationWarehouseId
        ) ?: return null

        return routeRaw.toDomain(originWarehouse, destinationWarehouse)
    }

    fun toDomain(rawRoutes: List<RouteRaw>): List<Route> {
        return rawRoutes.mapNotNull(::mapRoute)
    }
}

