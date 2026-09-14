package org.bytebloom.data.remote.mapper

import org.bytebloom.data.remote.dto.WarehouseDto
import org.bytebloom.domain.model.Warehouse

object WarehouseDtoMapper {

    fun toDomain(dto: WarehouseDto): Warehouse =
        Warehouse(
            id = dto.id,
            name = dto.name,
            regionalZone = dto.regionalZone,
            longitude = dto.longitude,
            latitude = dto.latitude
        )

    fun toDomainList(dtos: List<WarehouseDto>): List<Warehouse> =
        dtos.map(::toDomain)
}