package org.bytebloom.data.remote.datasource

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.bytebloom.data.remote.dto.warehouseDto.WarehouseRequestDto
import org.bytebloom.data.remote.dto.warehouseDto.WarehouseResponseDto
import org.bytebloom.data.source.remote.WarehouseRemoteDataSource
import org.bytebloom.data.remote.util.remoteCall

class SdkWarehouseDataSource(
    private val client: SupabaseClient,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : WarehouseRemoteDataSource {

    override suspend fun loadAll(): List<WarehouseResponseDto> =
        remoteCall("getAll warehouses", ioDispatcher) {
                client.from(TableName.WAREHOUSES).select().decodeList<WarehouseResponseDto>()
            }

    override suspend fun loadById(id: String): WarehouseResponseDto? =
        remoteCall("getById warehouse '$id'", ioDispatcher) {
                client.from(TableName.WAREHOUSES).select { filter { eq("id", id) } }.decodeSingleOrNull<WarehouseResponseDto>()
            }

    override suspend fun create(request: WarehouseRequestDto): WarehouseResponseDto? =
            remoteCall("create warehouse '${request.id}'") {
                client.from(TableName.WAREHOUSES).insert(request)
                loadById(request.id)
            }

    override suspend fun update(id: String, request: WarehouseRequestDto): WarehouseResponseDto? =
        remoteCall("update warehouse '$id'") {
                client.from(TableName.WAREHOUSES).update(request) { filter { eq("id", id) } }
                loadById(id)
            }

    override suspend fun delete(id: String): Boolean =
        remoteCall("delete warehouse '$id'") {
                client.from(TableName.WAREHOUSES).delete { filter { eq("id", id) } }
                true
            }
}