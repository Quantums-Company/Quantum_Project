package org.bytebloom.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import org.bytebloom.data.remote.client.SupabaseErrorTranslator
import org.bytebloom.data.remote.dto.packageDto.PackageRequestDto
import org.bytebloom.data.remote.dto.packageDto.PackageResponseDto
import org.bytebloom.data.source.remote.PackageRemoteDataSource

class SdkPackageDataSource(
    private val client: SupabaseClient
) : PackageRemoteDataSource {

    override suspend fun loadAll(): List<PackageResponseDto> =
        SupabaseErrorTranslator.translate("getAll packages") {
            client.from(TableName.PACKAGES).select().decodeList()
        }

    override suspend fun loadById(id: String): PackageResponseDto? =
        SupabaseErrorTranslator.translate("getById package '$id'") {
            client.from(TableName.PACKAGES)
                .select { filter { eq("id", id) } }
                .decodeSingleOrNull()
        }

    override suspend fun create(request: PackageRequestDto): PackageResponseDto? =
        SupabaseErrorTranslator.translate("create package '${request.id}'") {
            client.from(TableName.PACKAGES).insert(request)
            loadById(request.id)
        }


    override suspend fun update(id: String, request: PackageRequestDto): PackageResponseDto? =
        SupabaseErrorTranslator.translate("update package '${id}'") {
        client.from(TableName.PACKAGES).update(request) { filter { eq("id", id) } }
        loadById(id)
    }

    override suspend fun delete(id: String): Boolean =
        SupabaseErrorTranslator.translate("delete package '$id'") {
        client.from(TableName.PACKAGES).delete { filter { eq("id", id) } }
        true
    }
}