package org.bytebloom.logiroute.data.remote.datasource

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.bytebloom.logiroute.data.remote.dto.VehicleRequestDto
import org.bytebloom.logiroute.data.remote.dto.VehicleResponseDto
import org.bytebloom.logiroute.data.remote.util.RemoteExecutor

class SdkVehicleDataSource(
    private val client: SupabaseClient,
    private val remoteExecutor: RemoteExecutor,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : VehicleRemoteDataSource {

    private suspend fun fetchById(id: String): VehicleResponseDto? =
        client.from(TableName.VEHICLES.value)            .select {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingleOrNull<VehicleResponseDto>()

    override suspend fun loadAll(): List<VehicleResponseDto> =
        remoteExecutor.execute("getAll vehicles", ioDispatcher) {
            client.from(TableName.VEHICLES.value).select().decodeList<VehicleResponseDto>()
        }

    override suspend fun loadById(id: String): VehicleResponseDto? =
        remoteExecutor.execute("getById vehicle '$id'", ioDispatcher) {
            fetchById(id)
        }

    override suspend fun create(request: VehicleRequestDto): VehicleResponseDto? =
        remoteExecutor.execute("create vehicle '${request.id}'", ioDispatcher) {
            client.from(TableName.VEHICLES.value).insert(request)
            fetchById(request.id)
        }

    override suspend fun update(id: String, request: VehicleRequestDto): VehicleResponseDto? =
        remoteExecutor.execute("update vehicle '$id'", ioDispatcher) {
            client.from(TableName.VEHICLES.value).update(request) { filter { eq("id", id) } }
            fetchById(id)
        }

    override suspend fun delete(id: String): Boolean =
        remoteExecutor.execute("delete vehicle '$id'", ioDispatcher) {
            client.from(TableName.VEHICLES.value).delete { filter { eq("id", id) } }
            true
        }
}