package org.bytebloom.data.remote.mapper

import org.bytebloom.data.remote.dto.PackageDto
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Warehouse

object PackageDtoMapper {

    fun toDomain(
        dto: PackageDto,
        warehousesById: Map<String, Warehouse>
    ): Package? {
        val origin = warehousesById[dto.originWarehouseId] ?: return null
        val destination = warehousesById[dto.destinationWarehouseId] ?: return null

        return Package(
            id = dto.id,
            weight = dto.weight,
            priority = Priority.from(dto.priority),
            originWarehouse = origin,
            destinationWarehouse = destination
        )
    }

    fun toDomainList(
        dtos: List<PackageDto>,
        warehousesById: Map<String, Warehouse>
    ): List<Package> =
        dtos.mapNotNull { toDomain(it, warehousesById) }
}