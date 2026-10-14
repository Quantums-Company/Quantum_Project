package org.bytebloom.data.remote.datasource

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.bytebloom.data.remote.dto.vehicleDto.VehicleRequestDto
import org.bytebloom.data.remote.dto.vehicleDto.VehicleResponseDto
import org.bytebloom.data.source.remote.VehicleRemoteDataSource
import org.bytebloom.data.remote.util.remoteCall

class SdkVehicleDataSource(
    private val client: SupabaseClient,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : VehicleRemoteDataSource {

    override suspend fun loadAll(): List<VehicleResponseDto> =
            remoteCall("getAll vehicles") {
                client.from(TableName.VEHICLES).select().decodeList<VehicleResponseDto>()
            }

    override suspend fun loadById(id: String): VehicleResponseDto? =
            remoteCall("getById vehicle '$id'") {
                client.from(TableName.VEHICLES).select { filter { eq("id", id) } }.decodeSingleOrNull<VehicleResponseDto>()
            }

    override suspend fun create(request: VehicleRequestDto): VehicleResponseDto? =
            remoteCall("create vehicle '${request.id}'") {
                client.from(TableName.VEHICLES).insert(request)
                loadById(request.id)
            }

    override suspend fun update(id: String, request: VehicleRequestDto): VehicleResponseDto? =
            remoteCall("update vehicle '$id'") {
                client.from(TableName.VEHICLES).update(request) { filter { eq("id", id) } }
                loadById(id)
            }

    override suspend fun delete(id: String): Boolean =
            remoteCall("delete vehicle '$id'") {
                client.from(TableName.VEHICLES).delete { filter { eq("id", id) } }
                true
            }
}