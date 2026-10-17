package org.bytebloom.logiroute.data.remote.mapper

import org.bytebloom.logiroute.data.remote.dto.PackageResponseDto
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Priority
import org.bytebloom.logiroute.domain.model.Warehouse

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