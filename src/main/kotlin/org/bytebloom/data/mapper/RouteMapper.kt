package org.bytebloom.data.mapper

import org.bytebloom.data.raw.RouteRaw
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Warehouse

fun RouteRaw.toDomain(
    originWarehouse: Warehouse,
    destinationWarehouse: Warehouse
): Route {
    return Route(
        id = id,
        distanceKm = distanceKm,
        typicalDelayMin = typicalDelayMin,
        originWarehouse = originWarehouse,
        destinationWarehouse = destinationWarehouse
    )
}