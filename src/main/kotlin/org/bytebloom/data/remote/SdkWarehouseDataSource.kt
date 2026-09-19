package org.bytebloom.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import org.bytebloom.data.local.common.CsvColumns
import org.bytebloom.data.local.common.CsvTablesName
import org.bytebloom.data.local.common.hasExpectedColumns
import org.bytebloom.data.local.common.hasRequiredValues
import org.bytebloom.data.local.common.loadCsv
import org.bytebloom.data.local.common.toValidDouble
import org.bytebloom.data.raw.WarehouseRaw
import org.bytebloom.data.remote.dto.WarehouseRequestDto
import org.bytebloom.data.remote.dto.warehouseDto.WarehouseResponseDto
import org.bytebloom.data.source.WarehouseDataSource
import org.bytebloom.data.source.remote.WarehouseRemoteDataSource

class SdkWarehouseDataSource(
    private val client: SupabaseClient
): WarehouseRemoteDataSource {

    override suspend fun loadAll(): List<WarehouseResponseDto> =
        client.from(TableName.WAREHOUSES).select().decodeList()

    override suspend fun loadById(id: String): WarehouseResponseDto? =
        client.from(TableName.WAREHOUSES)
            .select { filter { eq("id", id) } }
            .decodeSingleOrNull()

    override suspend fun create(request: WarehouseRequestDto): WarehouseResponseDto? {
        client.from(TableName.WAREHOUSES).insert(request)
        return loadById(request.id)
    }

    override suspend fun update(id: String, request: WarehouseRequestDto): WarehouseResponseDto? {
        client.from(TableName.WAREHOUSES).update(request) { filter { eq("id", id) } }
        return loadById(id)
    }

    override suspend fun delete(id: String): Boolean {
        client.from(TableName.WAREHOUSES).delete { filter { eq("id", id) } }
        return true
    }
}