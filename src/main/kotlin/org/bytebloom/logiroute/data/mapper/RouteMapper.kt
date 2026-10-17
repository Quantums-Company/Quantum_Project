package org.bytebloom.logiroute.data.mapper

import org.bytebloom.logiroute.data.raw.RouteRaw
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

    private fun map(raw: RouteRaw): Route? {

        val origin = warehouseMapper.map(
            raw.originWarehouseId
        ) ?: return null

        val destination = warehouseMapper.map(
            raw.destinationWarehouseId
        ) ?: return null

        return raw.toDomain(origin, destination)
    }

    fun toDomain(routeRaws: List<RouteRaw>): List<Route> {
        return routeRaws.mapNotNull(::map)
    }
}

