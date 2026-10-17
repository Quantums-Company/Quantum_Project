package org.bytebloom.logiroute.data.repository.csv

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bytebloom.logiroute.data.mapper.VehicleMapper
import org.bytebloom.logiroute.data.mapper.WarehouseReferenceMapper
import org.bytebloom.logiroute.data.source.csv.VehicleDataSource
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.repository.VehicleRepository
import org.bytebloom.logiroute.domain.repository.WarehouseRepository

class CsvVehicleRepository(
    private val warehouseRepository: WarehouseRepository,
    private val csvVehicleDataSource: VehicleDataSource
) : VehicleRepository {

    private var cachedVehicles: List<Vehicle>? = null
    private val mutex = Mutex()

    private suspend fun loadAll(): List<Vehicle> {
        val warehousesById = warehouseRepository
            .getAll()
            .associateBy { it.id }

        val mapper = VehicleMapper(
            WarehouseReferenceMapper(warehousesById)
        )

        return mapper.toDomain(csvVehicleDataSource.loadAll())
    }

    suspend fun refresh(): List<Vehicle> =
        mutex.withLock {
            loadAll().also { vehicles ->
                cachedVehicles = vehicles
            }
        }

    override suspend fun getAll(): List<Vehicle> {
        cachedVehicles?.let { return it }

        return mutex.withLock {
            cachedVehicles
                ?: loadAll().also { vehicles ->
                    cachedVehicles = vehicles
                }
        }
    }

    override suspend fun getById(id: String): Vehicle? =
        getAll().find { it.id == id }

    override suspend fun create(vehicle: Vehicle): Vehicle =
        throw UnsupportedOperationException(
            "CSV repository is read-only. Create the vehicle through the remote repository."
        )

    override suspend fun update(vehicle: Vehicle): Vehicle =
        throw UnsupportedOperationException(
            "CSV repository is read-only. Update the vehicle through the remote repository."
        )

    override suspend fun delete(id: String): Boolean =
        throw UnsupportedOperationException(
            "CSV repository is read-only. Delete the vehicle through the remote repository."
        )
}
