package org.bytebloom.data.remote.datasource

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import org.bytebloom.data.remote.client.SupabaseErrorTranslator
import org.bytebloom.data.remote.dto.routeDto.RouteRequestDto
import org.bytebloom.data.remote.dto.routeDto.RouteResponseDto
import org.bytebloom.data.source.remote.RouteRemoteDataSource
import org.bytebloom.util.retryWithBackoff

class SdkRouteDataSource(
    private val client: SupabaseClient
) : RouteRemoteDataSource {

    override suspend fun loadAll(): List<RouteResponseDto> =
        retryWithBackoff {
            SupabaseErrorTranslator.translate("getAll routes") {
                client.from(TableName.ROUTES).select().decodeList<RouteResponseDto>()
            }
        }.getOrThrow()

    override suspend fun loadById(id: String): RouteResponseDto? =
        retryWithBackoff {
            SupabaseErrorTranslator.translate("getById route '$id'") {
                client.from(TableName.ROUTES)
                    .select { filter { eq("id", id) } }
                    .decodeSingleOrNull<RouteResponseDto>()
            }
        }.getOrThrow()

    override suspend fun create(request: RouteRequestDto): RouteResponseDto? =
        retryWithBackoff {
            SupabaseErrorTranslator.translate("create route '${request.id}'") {
                client.from(TableName.ROUTES).insert(request)
                loadById(request.id)
            }
        }.getOrThrow()

    override suspend fun update(id: String, request: RouteRequestDto): RouteResponseDto? =
        retryWithBackoff {
            SupabaseErrorTranslator.translate("update route '$id'") {
                client.from(TableName.ROUTES).update(request) { filter { eq("id", id) } }
                loadById(id)
            }
        }.getOrThrow()

    override suspend fun delete(id: String): Boolean =
        retryWithBackoff {
            SupabaseErrorTranslator.translate("delete route '$id'") {
                client.from(TableName.ROUTES).delete { filter { eq("id", id) } }
                true
            }
        }.getOrThrow()
}