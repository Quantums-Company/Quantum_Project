package org.bytebloom.data.repository.remote

import org.bytebloom.data.remote.dto.vehicleDto.VehicleRequestDto
import org.bytebloom.data.remote.mapper.VehicleDtoMapper
import org.bytebloom.data.source.remote.VehicleRemoteDataSource
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.util.retryWithBackoff
import java.time.Instant

class RemoteVehicleRepository(
    private val warehousesById: Map<String, Warehouse>,
    private val remoteDataSource: VehicleRemoteDataSource
) : VehicleRepository {

    override suspend fun getAll(): List<Vehicle> {
        val dtos = retryWithBackoff { remoteDataSource.loadAll() }.getOrThrow()
        return VehicleDtoMapper.toDomainList(dtos, warehousesById)
    }

    override suspend fun getById(id: String): Vehicle? {
        val dto = retryWithBackoff { remoteDataSource.loadById(id) }.getOrThrow()
        return dto?.let { VehicleDtoMapper.toDomain(it, warehousesById) }
    }

    override suspend fun create(vehicle: Vehicle): Vehicle {
        val dto = retryWithBackoff { remoteDataSource.create(vehicle.toRequestDto()) }.getOrThrow()
        return dto?.let { VehicleDtoMapper.toDomain(it, warehousesById) } ?: vehicle
    }

    override suspend fun update(vehicle: Vehicle): Vehicle {
        val dto = retryWithBackoff { remoteDataSource.update(vehicle.id, vehicle.toRequestDto()) }.getOrThrow()
        return dto?.let { VehicleDtoMapper.toDomain(it, warehousesById) } ?: vehicle
    }

    override suspend fun delete(id: String): Boolean {
        return retryWithBackoff { remoteDataSource.delete(id) }.getOrThrow()
    }

    private fun Vehicle.toRequestDto() = VehicleRequestDto(
        id = id, currentWarehouseId = currentWarehouse.id,
        maxCapacityKg = maxCapacityKg, costPerKm = costPerKm,
        updatedAt = Instant.now().toString()
    )
}