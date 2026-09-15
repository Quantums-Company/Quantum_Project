package org.bytebloom.data.remote.mapper

import org.bytebloom.data.remote.dto.warehouseDto.WarehouseResponseDto
import org.bytebloom.domain.model.Warehouse

object WarehouseDtoMapper {

    fun toDomain(dto: WarehouseResponseDto): Warehouse =
        Warehouse(
            id = dto.id,
            name = dto.name,
            regionalZone = dto.regionalZone,
            longitude = dto.longitude,
            latitude = dto.latitude
        )

    fun toDomainList(dtos: List<WarehouseResponseDto>): List<Warehouse> =
        dtos.map(::toDomain)
}
