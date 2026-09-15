package org.bytebloom.data.local.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import org.bytebloom.data.remote.dto.warehouseDto.WarehouseResponseDto
import org.bytebloom.data.remote.dto.WarehouseRequestDto
import org.bytebloom.data.remote.mapper.WarehouseDtoMapper
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.WarehouseRepository

class SupabaseWarehouseRepository(
    private val client: SupabaseClient
) : WarehouseRepository {

    override suspend fun getAll(): List<Warehouse> {
        val dtos = client.from(TableName.WAREHOUSES)
            .select()
            .decodeList<WarehouseResponseDto>()
        println("DEBUG: DTOs fetched from Supabase = ${dtos.size}")
        return WarehouseDtoMapper.toDomainList(dtos)
    }

    override suspend fun getById(id: String): Warehouse? {
        val dto = client.from(TableName.WAREHOUSES)
            .select { filter { eq("id", id) } }
            .decodeSingleOrNull<WarehouseResponseDto>()
        return dto?.let { WarehouseDtoMapper.toDomain(it) }
    }

    override suspend fun create(warehouse: Warehouse): Warehouse {
        val request = WarehouseRequestDto(
            id = warehouse.id,
            name = warehouse.name,
            regionalZone = warehouse.regionalZone,
            longitude = warehouse.longitude,
            latitude = warehouse.latitude
        )
        client.from(TableName.WAREHOUSES).insert(request)
        return warehouse
    }

    override suspend fun update(warehouse: Warehouse): Warehouse {
        val request = WarehouseRequestDto(
            id = warehouse.id,
            name = warehouse.name,
            regionalZone = warehouse.regionalZone,
            longitude = warehouse.longitude,
            latitude = warehouse.latitude
        )
        client.from(TableName.WAREHOUSES).update(request) {
            filter { eq("id", warehouse.id) }
        }
        return warehouse
    }

    override suspend fun delete(id: String): Boolean {
        client.from(TableName.WAREHOUSES).delete { filter { eq("id", id) } }
        return true
    }
}