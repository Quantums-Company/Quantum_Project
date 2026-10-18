package org.bytebloom.logiroute.data.remote.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.bytebloom.logiroute.data.remote.dto.VehicleRequestDto
import org.bytebloom.logiroute.data.remote.mapper.VehicleDtoMapper
import org.bytebloom.logiroute.data.remote.datasource.VehicleRemoteDataSource
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.repository.VehicleRepository
import org.bytebloom.logiroute.domain.repository.WarehouseRepository
import java.time.Instant

class RemoteVehicleRepository(
    private val warehouseRepository: WarehouseRepository,
    private val remoteDataSource: VehicleRemoteDataSource
) : VehicleRepository {

    private suspend fun <T> loadWithMapper(load: suspend () -> T): Pair<T, VehicleDtoMapper> = coroutineScope {
        val result = async { load() }
        val warehouses = async { warehouseRepository.getAll() }
        result.await() to VehicleDtoMapper(warehouses.await().associateBy { it.id } )
    }

    override suspend fun getAll(): List<Vehicle> {
        val (dto, mapper) = loadWithMapper { remoteDataSource.loadAll() }
        return mapper.mapList(dto).succeeded
    }

    override suspend fun getById(id: String): Vehicle? {
        val (dto, mapper) = loadWithMapper { remoteDataSource.loadById(id) }
        return dto?.let ( mapper::map )
    }

    override suspend fun create(vehicle: Vehicle): Vehicle {
        val (dto, mapper) = loadWithMapper { remoteDataSource.create(vehicle.toRequestDto()) }
        return dto?.let ( mapper::map ) ?: vehicle
    }

    override suspend fun update(vehicle: Vehicle): Vehicle {
        val (dto, mapper) = loadWithMapper { remoteDataSource.update(vehicle.id, vehicle.toRequestDto())}
        return dto?.let ( mapper::map ) ?: vehicle
    }

    override suspend fun delete(id: String): Boolean =
        remoteDataSource.delete(id)

    private fun Vehicle.toRequestDto() = VehicleRequestDto(
        id = id, currentWarehouseId = currentWarehouse.id,
        maxCapacityKg = maxCapacityKg, costPerKm = costPerKm,
        updatedAt = Instant.now().toString()
    )
}