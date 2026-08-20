package org.bytebloom.data.repository

import org.bytebloom.data.csv.DEFAULT_CSV_DIRECTORY
import org.bytebloom.data.csv.loadVehicles
import org.bytebloom.data.mapper.VehicleMapper
import org.bytebloom.data.mapper.WarehouseReferenceMapper
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.VehicleRepository

class CsvVehicleRepository(
    private val warehousesById: Map<String, Warehouse>,
    private val csvDirectory: String = DEFAULT_CSV_DIRECTORY
) : VehicleRepository {

    override fun getAll(): List<Vehicle> {
        val vehicleMapper = VehicleMapper(WarehouseReferenceMapper(warehousesById))

        return vehicleMapper.toDomain(loadVehicles(csvDirectory))
    }
}