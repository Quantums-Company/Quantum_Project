package org.bytebloom.data.remote.datasource

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import org.bytebloom.data.remote.client.SupabaseErrorTranslator
import org.bytebloom.data.remote.dto.vehicleDto.VehicleRequestDto
import org.bytebloom.data.remote.dto.vehicleDto.VehicleResponseDto
import org.bytebloom.data.source.remote.VehicleRemoteDataSource
import org.bytebloom.util.retryWithBackoff

class SdkVehicleDataSource(
    private val client: SupabaseClient
) : VehicleRemoteDataSource {

    override suspend fun loadAll(): List<VehicleResponseDto> =
        retryWithBackoff {
            SupabaseErrorTranslator.translate("getAll vehicles") {
                client.from(TableName.VEHICLES).select().decodeList<VehicleResponseDto>()
            }
        }.getOrThrow()

    override suspend fun loadById(id: String): VehicleResponseDto? =
        retryWithBackoff {
            SupabaseErrorTranslator.translate("getById vehicle '$id'") {
                client.from(TableName.VEHICLES)
                    .select { filter { eq("id", id) } }
                    .decodeSingleOrNull<VehicleResponseDto>()
            }
        }.getOrThrow()

    override suspend fun create(request: VehicleRequestDto): VehicleResponseDto? =
        retryWithBackoff {
            SupabaseErrorTranslator.translate("create vehicle '${request.id}'") {
                client.from(TableName.VEHICLES).insert(request)
                loadById(request.id)
            }
        }.getOrThrow()

    override suspend fun update(id: String, request: VehicleRequestDto): VehicleResponseDto? =
        retryWithBackoff {
            SupabaseErrorTranslator.translate("update vehicle '$id'") {
                client.from(TableName.VEHICLES).update(request) { filter { eq("id", id) } }
                loadById(id)
            }
        }.getOrThrow()

    override suspend fun delete(id: String): Boolean =
        retryWithBackoff {
            SupabaseErrorTranslator.translate("delete vehicle '$id'") {
                client.from(TableName.VEHICLES).delete { filter { eq("id", id) } }
                true
            }
        }.getOrThrow()
}