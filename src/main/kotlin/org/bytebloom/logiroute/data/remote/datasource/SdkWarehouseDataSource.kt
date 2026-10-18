package org.bytebloom.logiroute.data.remote.datasource

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.bytebloom.logiroute.data.remote.dto.WarehouseRequestDto
import org.bytebloom.logiroute.data.remote.dto.WarehouseResponseDto
import org.bytebloom.logiroute.data.remote.util.RemoteExecutor

class SdkWarehouseDataSource(
    private val client: SupabaseClient,
    private val remoteExecutor: RemoteExecutor,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : WarehouseRemoteDataSource {

    private suspend fun fetchById(id: String): WarehouseResponseDto? =
        client.from(TableName.WAREHOUSES)
            .select {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingleOrNull<WarehouseResponseDto>()

    override suspend fun loadAll(): List<WarehouseResponseDto> =
        remoteExecutor.execute("getAll warehouses", ioDispatcher) {
            client.from(TableName.WAREHOUSES).select().decodeList<WarehouseResponseDto>()
        }

    override suspend fun loadById(id: String): WarehouseResponseDto? =
        remoteExecutor.execute("getById warehouse '$id'", ioDispatcher) {
            fetchById(id)
        }

    override suspend fun create(request: WarehouseRequestDto): WarehouseResponseDto? =
        remoteExecutor.execute("create warehouse '${request.id}'", ioDispatcher) {
            client.from(TableName.WAREHOUSES).insert(request)
            fetchById(request.id)
        }

    override suspend fun update(id: String, request: WarehouseRequestDto): WarehouseResponseDto? =
        remoteExecutor.execute("update warehouse '$id'", ioDispatcher) {
            client.from(TableName.WAREHOUSES).update(request) { filter { eq("id", id) } }
            fetchById(id)
        }

    override suspend fun delete(id: String): Boolean =
        remoteExecutor.execute("delete warehouse '$id'", ioDispatcher) {
            client.from(TableName.WAREHOUSES).delete { filter { eq("id", id) } }
            true
        }
}