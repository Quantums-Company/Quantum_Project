package org.bytebloom.data.repository

import org.bytebloom.data.mapper.VehicleMapper
import org.bytebloom.data.mapper.WarehouseReferenceMapper
import org.bytebloom.data.source.VehicleDataSource
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.util.Logger

class CsvVehicleRepository(
    private val warehousesById: Map<String, Warehouse>,
    private val csvVehicleDataSource: VehicleDataSource
) : VehicleRepository {
    private var cachedVehicles= listOf<Vehicle>()

    private fun loadAll():List<Vehicle>{
        val vehicleMapper = VehicleMapper(WarehouseReferenceMapper(warehousesById))
        val vehicleRaws = csvVehicleDataSource.loadAll()

        return vehicleMapper.toDomain(vehicleRaws)
    }

    fun refresh(){
        cachedVehicles = loadAll()
    }

    init {
        Logger.info("Loading vehicles in init...")
        cachedVehicles = loadAll()
    }
    override fun getAll(): List<Vehicle> = cachedVehicles
    override fun getById(id: String): Vehicle? {
        TODO("Not yet implemented")
    }

    override fun create(vehicle: Vehicle): Vehicle {
        TODO("Not yet implemented")
    }

    override fun update(vehicle: Vehicle): Vehicle {
        TODO("Not yet implemented")
    }

    override fun delete(id: String): Boolean {
        TODO("Not yet implemented")
    }
}