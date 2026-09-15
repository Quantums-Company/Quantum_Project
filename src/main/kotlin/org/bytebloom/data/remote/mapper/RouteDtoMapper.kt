package org.bytebloom.data.remote.mapper

import org.bytebloom.data.remote.dto.RouteDto
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Warehouse

object RouteDtoMapper {

    fun toDomain(
        dto: RouteDto,
        warehousesById: Map<String, Warehouse>
    ): Route? {
        val origin = warehousesById[dto.originWarehouseId] ?: return null
        val destination = warehousesById[dto.destinationWarehouseId] ?: return null

        return Route(
            id = dto.id,
            distanceKm = dto.distanceKm,
            typicalDelayMin = dto.typicalDelayMin,
            originWarehouse = origin,
            destinationWarehouse = destination
        )
    }

    fun toDomainList(
        dtos: List<RouteDto>,
        warehousesById: Map<String, Warehouse>
    ): List<Route> =
        dtos.mapNotNull { toDomain(it, warehousesById) }
}