package org.bytebloom.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import org.bytebloom.data.remote.dto.vehicleDto.VehicleRequestDto
import org.bytebloom.data.remote.dto.vehicleDto.VehicleResponseDto
import org.bytebloom.data.source.remote.VehicleRemoteDataSource

class SdkVehicleDataSource(
    private val client: SupabaseClient
) : VehicleRemoteDataSource {

    override suspend fun loadAll(): List<VehicleResponseDto> =
        client.from(TableName.VEHICLES).select().decodeList()

    override suspend fun loadById(id: String): VehicleResponseDto? =
        client.from(TableName.VEHICLES)
            .select { filter { eq("id", id) } }
            .decodeSingleOrNull()

    override suspend fun create(request: VehicleRequestDto): VehicleResponseDto? {
        client.from(TableName.VEHICLES).insert(request)
        return loadById(request.id)
    }

    override suspend fun update(id: String, request: VehicleRequestDto): VehicleResponseDto? {
        client.from(TableName.VEHICLES).update(request) { filter { eq("id", id) } }
        return loadById(id)
    }

    override suspend fun delete(id: String): Boolean {
        client.from(TableName.VEHICLES).delete { filter { eq("id", id) } }
        return true
    }
}