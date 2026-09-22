package org.bytebloom.data.remote.datasource

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import org.bytebloom.data.remote.client.SupabaseErrorTranslator
import org.bytebloom.data.remote.dto.packageDto.PackageRequestDto
import org.bytebloom.data.remote.dto.packageDto.PackageResponseDto
import org.bytebloom.data.source.remote.PackageRemoteDataSource
import org.bytebloom.util.retryWithBackoff

class SdkPackageDataSource(
    private val client: SupabaseClient
) : PackageRemoteDataSource {

    override suspend fun loadAll(): List<PackageResponseDto> =
        retryWithBackoff {
            SupabaseErrorTranslator.translate("getAll packages") {
                client.from(TableName.PACKAGES).select().decodeList<PackageResponseDto>()
            }
        }.getOrThrow()

    override suspend fun loadById(id: String): PackageResponseDto? =
        retryWithBackoff {
            SupabaseErrorTranslator.translate("getById package '$id'") {
                client.from(TableName.PACKAGES)
                    .select { filter { eq("id", id) } }
                    .decodeSingleOrNull<PackageResponseDto>()
            }
        }.getOrThrow()

    override suspend fun create(request: PackageRequestDto): PackageResponseDto? =
        retryWithBackoff {
            SupabaseErrorTranslator.translate("create package '${request.id}'") {
                client.from(TableName.PACKAGES).insert(request)
                loadById(request.id)
            }
        }.getOrThrow()

    override suspend fun update(id: String, request: PackageRequestDto): PackageResponseDto? =
        retryWithBackoff {
            SupabaseErrorTranslator.translate("update package '$id'") {
                client.from(TableName.PACKAGES).update(request) { filter { eq("id", id) } }
                loadById(id)
            }
        }.getOrThrow()

    override suspend fun delete(id: String): Boolean =
        retryWithBackoff {
            SupabaseErrorTranslator.translate("delete package '$id'") {
                client.from(TableName.PACKAGES).delete { filter { eq("id", id) } }
                true
            }
        }.getOrThrow()
}