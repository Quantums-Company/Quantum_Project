package org.bytebloom.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import org.bytebloom.data.remote.client.SupabaseErrorTranslator
import org.bytebloom.data.remote.dto.routeDto.RouteRequestDto
import org.bytebloom.data.remote.dto.routeDto.RouteResponseDto
import org.bytebloom.data.source.remote.RouteRemoteDataSource

class SdkRouteDataSource(
    private val client: SupabaseClient
) : RouteRemoteDataSource {

    override suspend fun loadAll(): List<RouteResponseDto> =
        SupabaseErrorTranslator.translate("getAll routes") {
            client.from(TableName.ROUTES).select().decodeList()
        }

    override suspend fun loadById(id: String): RouteResponseDto? =
        SupabaseErrorTranslator.translate("getById route '$id'") {
            client.from(TableName.ROUTES)
                .select { filter { eq("id", id) } }
                .decodeSingleOrNull()
        }

    override suspend fun create(request: RouteRequestDto): RouteResponseDto? =
        SupabaseErrorTranslator.translate("create route '${request.id}'") {
        client.from(TableName.ROUTES).insert(request)
        loadById(request.id)
    }

    override suspend fun update(id: String, request: RouteRequestDto): RouteResponseDto? =
        SupabaseErrorTranslator.translate("update route '${id}'") {
        client.from(TableName.ROUTES).update(request) { filter { eq("id", id) } }
        loadById(id)
    }

    override suspend fun delete(id: String): Boolean =
        SupabaseErrorTranslator.translate("delete route '$id'") {
        client.from(TableName.ROUTES).delete { filter { eq("id", id) } }
        true
    }
}