package org.bytebloom.data.repository.csv

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bytebloom.data.mapper.VehicleMapper
import org.bytebloom.data.mapper.WarehouseReferenceMapper
import org.bytebloom.data.source.csv.VehicleDataSource
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.util.Logger

class CsvVehicleRepository(
    private val warehousesById: Map<String, Warehouse>,
    private val csvVehicleDataSource: VehicleDataSource
) : VehicleRepository {
    private var cachedVehicles= listOf<Vehicle>()

    private suspend fun loadAll():List<Vehicle>{
        val vehicleMapper = VehicleMapper(WarehouseReferenceMapper(warehousesById))
        val vehicleRaws = csvVehicleDataSource.loadAll()

        return vehicleMapper.toDomain(vehicleRaws)
    }

    suspend fun refresh(){
        cachedVehicles = loadAll()
    }

    init {
        Logger.info("Loading vehicles in init...")
        CoroutineScope(Dispatchers.IO).launch {
            refresh()
        }
    }
    override suspend fun getAll(): List<Vehicle> = cachedVehicles
    override suspend fun getById(id: String): Vehicle? {
        TODO("Not yet implemented")
    }

    override suspend fun create(vehicle: Vehicle): Vehicle {
        TODO("Not yet implemented")
    }

    override suspend fun update(vehicle: Vehicle): Vehicle {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: String): Boolean {
        TODO("Not yet implemented")
    }
}