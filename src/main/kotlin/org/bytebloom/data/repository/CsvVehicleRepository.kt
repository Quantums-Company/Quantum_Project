package org.bytebloom.data.repository

import org.bytebloom.data.csv.loadVehicles
import org.bytebloom.data.lookup.findWarehouse
import org.bytebloom.data.mapper.toDomain
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.VehicleRepository

class CsvVehicleRepository(
    private val warehousesById: Map<String, Warehouse>
) : VehicleRepository {
    override fun getAllVehicles(): List<Vehicle> {

        return loadVehicles().mapNotNull { raw ->

            val currentWarehouse =warehousesById.findWarehouse(
                warehouseId = raw.currentWarehouseId,
                owner = "Route",
                ownerId = raw.id
            )
            if (currentWarehouse == null) {
                return@mapNotNull null
            }

            raw.toDomain(currentWarehouse)
        }
    }
}