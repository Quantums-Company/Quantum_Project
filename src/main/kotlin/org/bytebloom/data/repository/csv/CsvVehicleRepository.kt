package org.bytebloom.data.repository.csv

import org.bytebloom.data.mapper.VehicleMapper
import org.bytebloom.data.mapper.WarehouseReferenceMapper
import org.bytebloom.data.source.csv.VehicleDataSource
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.VehicleRepository
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.Mutex

class CsvVehicleRepository(
    private val warehousesById: Map<String, Warehouse>,
    private val csvVehicleDataSource: VehicleDataSource
) : VehicleRepository {
    private var cachedVehicles: List<Vehicle>? = null
    private val mutex = Mutex()

    private suspend fun loadAll():List<Vehicle>{
        val vehicleMapper = VehicleMapper(WarehouseReferenceMapper(warehousesById))

        return vehicleMapper.toDomain(csvVehicleDataSource.loadAll())
    }

    suspend fun refresh(){
        mutex.withLock { cachedVehicles = loadAll() }
    }

    override suspend fun getAll(): List<Vehicle> =
        cachedVehicles ?:  mutex.withLock { cachedVehicles ?: loadAll().also { cachedVehicles = it }
    }

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