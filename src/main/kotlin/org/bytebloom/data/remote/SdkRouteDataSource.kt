package org.bytebloom.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import org.bytebloom.data.remote.dto.routeDto.RouteRequestDto
import org.bytebloom.data.remote.dto.routeDto.RouteResponseDto
import org.bytebloom.data.source.remote.RouteRemoteDataSource

class SdkRouteDataSource(
    private val client: SupabaseClient
) : RouteRemoteDataSource {

    override suspend fun loadAll(): List<RouteResponseDto> =
        client.from(TableName.ROUTES).select().decodeList()

    override suspend fun loadById(id: String): RouteResponseDto? =
        client.from(TableName.ROUTES)
            .select { filter { eq("id", id) } }
            .decodeSingleOrNull()

    override suspend fun create(request: RouteRequestDto): RouteResponseDto? {
        client.from(TableName.ROUTES).insert(request)
        return loadById(request.id)
    }

    override suspend fun update(id: String, request: RouteRequestDto): RouteResponseDto? {
        client.from(TableName.ROUTES).update(request) { filter { eq("id", id) } }
        return loadById(id)
    }

    override suspend fun delete(id: String): Boolean {
        client.from(TableName.ROUTES).delete { filter { eq("id", id) } }
        return true
    }
}