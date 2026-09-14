package org.bytebloom.data.remote.mapper

import org.bytebloom.data.remote.dto.VehicleDto
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse

object VehicleDtoMapper {

    fun toDomain(
        dto: VehicleDto,
        warehousesById: Map<String, Warehouse>
    ): Vehicle? {
        val currentWarehouse = warehousesById[dto.currentWarehouseId] ?: return null

        return Vehicle(
            id = dto.id,
            maxCapacityKg = dto.maxCapacityKg,
            costPerKm = dto.costPerKm,
            currentWarehouse = currentWarehouse
        )
    }

    fun toDomainList(
        dtos: List<VehicleDto>,
        warehousesById: Map<String, Warehouse>
    ): List<Vehicle> =
        dtos.mapNotNull { toDomain(it, warehousesById) }
}
