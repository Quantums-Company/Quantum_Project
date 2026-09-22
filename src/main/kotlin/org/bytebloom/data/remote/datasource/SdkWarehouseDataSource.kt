package org.bytebloom.data.remote.datasource

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import org.bytebloom.data.remote.client.SupabaseErrorTranslator
import org.bytebloom.data.remote.dto.warehouseDto.WarehouseRequestDto
import org.bytebloom.data.remote.dto.warehouseDto.WarehouseResponseDto
import org.bytebloom.data.source.remote.WarehouseRemoteDataSource
import org.bytebloom.util.retryWithBackoff

class SdkWarehouseDataSource(
    private val client: SupabaseClient
) : WarehouseRemoteDataSource {

    override suspend fun loadAll(): List<WarehouseResponseDto> =
        retryWithBackoff {
            SupabaseErrorTranslator.translate("getAll warehouses") {
                client.from(TableName.WAREHOUSES).select().decodeList<WarehouseResponseDto>()
            }
        }.getOrThrow()

    override suspend fun loadById(id: String): WarehouseResponseDto? =
        retryWithBackoff {
            SupabaseErrorTranslator.translate("getById warehouse '$id'") {
                client.from(TableName.WAREHOUSES)
                    .select { filter { eq("id", id) } }
                    .decodeSingleOrNull<WarehouseResponseDto>()
            }
        }.getOrThrow()

    override suspend fun create(request: WarehouseRequestDto): WarehouseResponseDto? =
        retryWithBackoff {
            SupabaseErrorTranslator.translate("create warehouse '${request.id}'") {
                client.from(TableName.WAREHOUSES).insert(request)
                loadById(request.id)
            }
        }.getOrThrow()

    override suspend fun update(id: String, request: WarehouseRequestDto): WarehouseResponseDto? =
        retryWithBackoff {
            SupabaseErrorTranslator.translate("update warehouse '$id'") {
                client.from(TableName.WAREHOUSES).update(request) { filter { eq("id", id) } }
                loadById(id)
            }
        }.getOrThrow()

    override suspend fun delete(id: String): Boolean =
        retryWithBackoff {
            SupabaseErrorTranslator.translate("delete warehouse '$id'") {
                client.from(TableName.WAREHOUSES).delete { filter { eq("id", id) } }
                true
            }
        }.getOrThrow()
}