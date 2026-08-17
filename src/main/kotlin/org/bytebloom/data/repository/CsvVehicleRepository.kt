package org.bytebloom.data.repository

import org.bytebloom.data.csv.loadVehicles
import org.bytebloom.data.mapper.toDomain
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.VehicleRepository

class CsvVehicleRepository(
    private val warehouseMap: Map<String, Warehouse>
) : VehicleRepository {
    override fun getAllVehicles(): List<Vehicle> {
        return loadVehicles().mapNotNull { it.toDomain(warehouseMap) }
    }
}