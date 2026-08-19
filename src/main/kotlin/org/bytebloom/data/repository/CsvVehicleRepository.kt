package org.bytebloom.data.repository

import org.bytebloom.data.csv.DEFAULT_CSV_DIRECTORY
import org.bytebloom.data.csv.loadVehicles
import org.bytebloom.data.lookup.findWarehouse
import org.bytebloom.data.mapper.toDomain
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.VehicleRepository

class CsvVehicleRepository(
    private val warehousesById: Map<String, Warehouse>,
    private val csvDirectory: String = DEFAULT_CSV_DIRECTORY
) : VehicleRepository {

    override fun getAll(): List<Vehicle> {

        return loadVehicles(csvDirectory).mapNotNull { raw ->

            val currentWarehouse = warehousesById.findWarehouse(
                warehouseId = raw.currentWarehouseId,
                owner = "Vehicle",
                ownerId = raw.id
            )
            if (currentWarehouse == null) {
                return@mapNotNull null
            }

            raw.toDomain(currentWarehouse)
        }
    }
}