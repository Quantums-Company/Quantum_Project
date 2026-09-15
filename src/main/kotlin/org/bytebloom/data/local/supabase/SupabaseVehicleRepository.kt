package org.bytebloom.data.local.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import org.bytebloom.data.remote.dto.vehicleDto.VehicleRequestDto
import org.bytebloom.data.remote.dto.vehicleDto.VehicleResponseDto
import org.bytebloom.data.remote.mapper.VehicleDtoMapper
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.repository.WarehouseRepository

class SupabaseVehicleRepository(
    private val client: SupabaseClient,
    private val warehouseRepository: WarehouseRepository
) : VehicleRepository {

    private suspend fun warehousesById() =
        warehouseRepository.getAll().associateBy { it.id }

    override suspend fun getAll(): List<Vehicle> {
        val dtos = client.from(TableName.VEHICLES)
            .select()
            .decodeList<VehicleResponseDto>()
        println("DEBUG: DTOs fetched from Supabase = ${dtos.size}")
        return VehicleDtoMapper.toDomainList(dtos, warehousesById())
    }

    override suspend fun getById(id: String): Vehicle? {
        val dto = client.from(TableName.VEHICLES)
            .select { filter { eq("id", id) } }
            .decodeSingleOrNull<VehicleResponseDto>()
        return dto?.let { VehicleDtoMapper.toDomain(it, warehousesById()) }
    }

    override suspend fun create(vehicle: Vehicle): Vehicle {
        val request = VehicleRequestDto(
            id = vehicle.id,
            currentWarehouseId = vehicle.currentWarehouse.id,
            maxCapacityKg = vehicle.maxCapacityKg,
            costPerKm = vehicle.costPerKm
        )
        client.from(TableName.VEHICLES).insert(request)
        return vehicle
    }

    override suspend fun update(vehicle: Vehicle): Vehicle {
        val request = VehicleRequestDto(
            id = vehicle.id,
            currentWarehouseId = vehicle.currentWarehouse.id,
            maxCapacityKg = vehicle.maxCapacityKg,
            costPerKm = vehicle.costPerKm
        )
        client.from(TableName.VEHICLES).update(request) {
            filter { eq("id", vehicle.id) }
        }
        return vehicle
    }

    override suspend fun delete(id: String): Boolean {
        client.from(TableName.VEHICLES).delete { filter { eq("id", id) } }
        return true
    }
}