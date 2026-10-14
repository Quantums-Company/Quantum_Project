package org.bytebloom.data.remote.datasource

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.bytebloom.data.remote.client.SupabaseErrorTranslator
import org.bytebloom.data.remote.dto.routeDto.RouteRequestDto
import org.bytebloom.data.remote.dto.routeDto.RouteResponseDto
import org.bytebloom.data.source.remote.RouteRemoteDataSource
import org.bytebloom.data.remote.util.retryWithBackoff
import org.bytebloom.data.remote.util.remoteCall

class SdkRouteDataSource(
    private val client: SupabaseClient,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : RouteRemoteDataSource {

    override suspend fun loadAll(): List<RouteResponseDto> =
            remoteCall("getAll routes") {
                client.from(TableName.ROUTES).select().decodeList<RouteResponseDto>()
            }

    override suspend fun loadById(id: String): RouteResponseDto? =
            remoteCall("getById route '$id'") {
                client.from(TableName.ROUTES).select { filter { eq("id", id) } }.decodeSingleOrNull<RouteResponseDto>()
            }

    override suspend fun create(request: RouteRequestDto): RouteResponseDto? =
            remoteCall("create route '${request.id}'") {
                client.from(TableName.ROUTES).insert(request)
                loadById(request.id)
            }

    override suspend fun update(id: String, request: RouteRequestDto): RouteResponseDto? =
            remoteCall("update route '$id'") {
                client.from(TableName.ROUTES).update(request) { filter { eq("id", id) } }
                loadById(id)
            }

    override suspend fun delete(id: String): Boolean =
            remoteCall("delete route '$id'") {
                client.from(TableName.ROUTES).delete { filter { eq("id", id) } }
                true
            }
}