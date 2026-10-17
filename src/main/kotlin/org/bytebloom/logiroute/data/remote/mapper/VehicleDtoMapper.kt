package org.bytebloom.logiroute.data.remote.mapper

import org.bytebloom.logiroute.data.remote.dto.vehicleDto.VehicleResponseDto
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse

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