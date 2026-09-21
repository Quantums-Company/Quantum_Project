package org.bytebloom.data.remote.mapper

import org.bytebloom.data.remote.dto.vehicleDto.VehicleResponseDto
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse

class VehicleDtoMapper(
    warehousesById: Map<String, Warehouse>
) : EntityMapper<VehicleResponseDto, Vehicle>(
    toDomain = { dto ->
        warehousesById[dto.currentWarehouseId]?.let { currentWarehouse ->
            Vehicle(
                id = dto.id,
                maxCapacityKg = dto.maxCapacityKg,
                costPerKm = dto.costPerKm,
                currentWarehouse = currentWarehouse
            )
        }
    }
)