package org.bytebloom.data.repository

import org.bytebloom.data.csv.DEFAULT_CSV_DIRECTORY
import org.bytebloom.data.csv.loadVehicles
import org.bytebloom.data.mapper.VehicleMapper
import org.bytebloom.data.mapper.WarehouseReferenceMapper
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.util.Logger

class CsvVehicleRepository(
    private val warehousesById: Map<String, Warehouse>,
    private val csvDirectory: String = DEFAULT_CSV_DIRECTORY
) : VehicleRepository {
    private var achedVehicles= listOf<Vehicle>()

    private fun loadAll():List<Vehicle>{
        val vehicleMapper = VehicleMapper(WarehouseReferenceMapper(warehousesById))
        val vehicleRaws = loadVehicles(csvDirectory)

        return vehicleMapper.toDomain(vehicleRaws)
    }

    fun refresh(){
        achedVehicles = loadAll()
    }

    init {
        Logger.info("Loading vehicles in init...")
        achedVehicles = loadAll()
    }
    override fun getAll(): List<Vehicle> = achedVehicles
}