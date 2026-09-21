package org.bytebloom.data.remote.mapper

import org.bytebloom.data.remote.dto.routeDto.RouteResponseDto
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Warehouse

class RouteDtoMapper(
    warehousesById: Map<String, Warehouse>
) : EntityMapper<RouteResponseDto, Route>(
    toDomain = { dto ->
        val origin = warehousesById[dto.originWarehouseId]
        val destination = warehousesById[dto.destinationWarehouseId]

        if (origin != null && destination != null) {
            Route(
                id = dto.id,
                distanceKm = dto.distanceKm,
                typicalDelayMin = dto.typicalDelayMin,
                originWarehouse = origin,
                destinationWarehouse = destination
            )
        } else null
    }
)
