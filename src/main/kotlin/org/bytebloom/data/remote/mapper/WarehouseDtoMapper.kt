package org.bytebloom.data.remote.mapper

import org.bytebloom.data.remote.dto.warehouseDto.WarehouseResponseDto
import org.bytebloom.domain.model.Warehouse

class WarehouseDtoMapper : EntityMapper<WarehouseResponseDto, Warehouse>(
    toDomain = { dto ->
        Warehouse(
            id = dto.id,
            name = dto.name,
            regionalZone = dto.regionalZone,
            longitude = dto.longitude,
            latitude = dto.latitude
        )
    }
)