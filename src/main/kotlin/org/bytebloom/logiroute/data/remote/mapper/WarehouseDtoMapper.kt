package org.bytebloom.logiroute.data.remote.mapper

import org.bytebloom.logiroute.data.remote.dto.warehouseDto.WarehouseResponseDto
import org.bytebloom.logiroute.domain.model.Warehouse

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