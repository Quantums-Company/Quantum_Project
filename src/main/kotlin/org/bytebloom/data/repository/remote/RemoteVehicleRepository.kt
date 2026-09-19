package org.bytebloom.data.repository.remote

import org.bytebloom.data.remote.dto.vehicleDto.VehicleRequestDto
import org.bytebloom.data.remote.mapper.VehicleDtoMapper
import org.bytebloom.data.source.remote.VehicleRemoteDataSource
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.VehicleRepository

class RemoteVehicleRepository(
    private val warehousesById: Map<String, Warehouse>,
    private val remoteDataSource: VehicleRemoteDataSource
) : VehicleRepository {

    override suspend fun getAll(): List<Vehicle> =
        VehicleDtoMapper.toDomainList(remoteDataSource.loadAll(), warehousesById)

    override suspend fun getById(id: String): Vehicle? =
        remoteDataSource.loadById(id)?.let { VehicleDtoMapper.toDomain(it, warehousesById) }

    override suspend fun create(vehicle: Vehicle): Vehicle {
        val dto = remoteDataSource.create(vehicle.toRequestDto())
        return dto?.let { VehicleDtoMapper.toDomain(it, warehousesById) } ?: vehicle
    }

    override suspend fun update(vehicle: Vehicle): Vehicle {
        val dto = remoteDataSource.update(vehicle.id, vehicle.toRequestDto())
        return dto?.let { VehicleDtoMapper.toDomain(it, warehousesById) } ?: vehicle
    }

    override suspend fun delete(id: String): Boolean =
        remoteDataSource.delete(id)

    private fun Vehicle.toRequestDto() = VehicleRequestDto(
        id = id, currentWarehouseId = currentWarehouse.id,
        maxCapacityKg = maxCapacityKg, costPerKm = costPerKm
    )
}