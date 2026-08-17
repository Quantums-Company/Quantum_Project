package org.bytebloom.data.mapper



import org.bytebloom.data.raw.RouteRaw
import org.bytebloom.data.raw.WarehouseRaw
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Warehouse

fun RouteRaw.toDomain(warehouse: WarehouseRaw): Route? {


    return Route(
        id = id,
        distanceKm = distanceKm,
        typicalDelayMin = typicalDelayMin,
        originWarehouse = warehouse.toDomain(),
        destinationWarehouse = warehouse.toDomain()
    )
}