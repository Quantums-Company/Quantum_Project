package org.bytebloom.data.remote.datasource

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.bytebloom.data.remote.dto.vehicleDto.VehicleRequestDto
import org.bytebloom.data.remote.dto.vehicleDto.VehicleResponseDto
import org.bytebloom.data.remote.util.RemoteExecutor
import org.bytebloom.data.source.remote.VehicleRemoteDataSource

class SdkVehicleDataSource(
    private val client: SupabaseClient,
    private val remoteExecutor: RemoteExecutor,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : VehicleRemoteDataSource {

    override suspend fun loadAll(): List<VehicleResponseDto> =
        remoteExecutor.execute("getAll vehicles", ioDispatcher) {
            client.from(TableName.VEHICLES).select().decodeList<VehicleResponseDto>()
        }

    override suspend fun loadById(id: String): VehicleResponseDto? =
        remoteExecutor.execute("getById vehicle '$id'", ioDispatcher) {
            client.from(TableName.VEHICLES).select { filter { eq("id", id) } }.
            decodeSingleOrNull<VehicleResponseDto>()
        }

    override suspend fun create(request: VehicleRequestDto): VehicleResponseDto? =
        remoteExecutor.execute("create vehicle '${request.id}'", ioDispatcher) {
            client.from(TableName.VEHICLES).insert(request)
            loadById(request.id)
        }

    override suspend fun update(id: String, request: VehicleRequestDto): VehicleResponseDto? =
        remoteExecutor.execute("update vehicle '$id'", ioDispatcher) {
            client.from(TableName.VEHICLES).update(request) { filter { eq("id", id) } }
            loadById(id)
        }

    override suspend fun delete(id: String): Boolean =
        remoteExecutor.execute("delete vehicle '$id'", ioDispatcher) {
            client.from(TableName.VEHICLES).delete { filter { eq("id", id) } }
            true
        }
}