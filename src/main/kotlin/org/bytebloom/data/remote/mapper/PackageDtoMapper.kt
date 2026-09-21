package org.bytebloom.data.remote.mapper

import org.bytebloom.data.remote.dto.packageDto.PackageResponseDto
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Warehouse

class PackageDtoMapper(
    warehousesById: Map<String, Warehouse>
) : EntityMapper<PackageResponseDto, Package>(
    toDomain = { dto ->
        val origin = warehousesById[dto.originWarehouseId]
        val destination = warehousesById[dto.destinationWarehouseId]

        if (origin != null && destination != null) {
            Package(
                id = dto.id,
                weight = dto.weight,
                priority = Priority.from(dto.priority),
                originWarehouse = origin,
                destinationWarehouse = destination
            )
        } else null
    }
)