package org.bytebloom.data.repository.remote

import org.bytebloom.data.remote.dto.vehicleDto.VehicleRequestDto
import org.bytebloom.data.remote.mapper.VehicleDtoMapper
import org.bytebloom.data.source.remote.VehicleRemoteDataSource
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.repository.WarehouseRepository
import java.time.Instant

class RemoteVehicleRepository(
    private val warehouseRepository: WarehouseRepository,
    private val remoteDataSource: VehicleRemoteDataSource
) : VehicleRepository {

    private suspend fun mapper() = VehicleDtoMapper(warehouseRepository.getAll().associateBy { it.id })

    override suspend fun getAll(): List<Vehicle> =
        mapper() .mapList(remoteDataSource.loadAll()).succeeded

    override suspend fun getById(id: String): Vehicle? =
        remoteDataSource.loadById(id)?.let { mapper() .map(it) }

    override suspend fun create(vehicle: Vehicle): Vehicle {
        val dto = remoteDataSource.create(vehicle.toRequestDto())
        return dto?.let { mapper() .map(it) } ?: vehicle
    }

    override suspend fun update(vehicle: Vehicle): Vehicle {
        val dto = remoteDataSource.update(vehicle.id, vehicle.toRequestDto())
        return dto?.let { mapper() .map(it) } ?: vehicle
    }

    override suspend fun delete(id: String): Boolean =
        remoteDataSource.delete(id)

    private fun Vehicle.toRequestDto() = VehicleRequestDto(
        id = id, currentWarehouseId = currentWarehouse.id,
        maxCapacityKg = maxCapacityKg, costPerKm = costPerKm,
        updatedAt = Instant.now().toString()
    )
}