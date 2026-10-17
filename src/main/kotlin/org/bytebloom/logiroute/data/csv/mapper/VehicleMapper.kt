package org.bytebloom.logiroute.data.csv.mapper

import org.bytebloom.logiroute.data.csv.raw.VehicleRaw
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse

class VehicleMapper(
    private val warehouseMapper: WarehouseReferenceMapper
) {
    private fun VehicleRaw.toDomain(
        currentWarehouse: Warehouse
    ): Vehicle {
        val vehicle = Vehicle(
            id = id,
            maxCapacityKg = maxCapacityKg,
            costPerKm = costPerKm,
            currentWarehouse = currentWarehouse
        )

        vehicle.currentWarehouse.addVehicle(vehicle)

        return vehicle
    }

    private fun map(raw: VehicleRaw): Vehicle? {
        val currentWarehouse = warehouseMapper.map(
            raw.currentWarehouseId
        ) ?: return null

        return raw.toDomain(currentWarehouse)
    }

    fun toDomain(vehicleRaws: List<VehicleRaw>): List<Vehicle> {
        return vehicleRaws.mapNotNull(::map)
    }
}

