package org.bytebloom.logiroute.data.remote.datasource

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.bytebloom.logiroute.data.remote.dto.RouteRequestDto
import org.bytebloom.logiroute.data.remote.dto.RouteResponseDto
import org.bytebloom.logiroute.data.remote.util.RemoteExecutor

class SdkRouteDataSource(
    private val client: SupabaseClient,
    private val remoteExecutor: RemoteExecutor,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : RouteRemoteDataSource {

    private suspend fun fetchById(id: String): RouteResponseDto? =
        client.from(TableName.ROUTES)
            .select {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingleOrNull<RouteResponseDto>()

    override suspend fun loadAll(): List<RouteResponseDto> =
        remoteExecutor.execute("getAll routes", ioDispatcher) {
            client.from(TableName.ROUTES).select().decodeList<RouteResponseDto>()
        }

    override suspend fun loadById(id: String): RouteResponseDto? =
        remoteExecutor.execute("getById route '$id'", ioDispatcher) {
            fetchById(id)
        }

    override suspend fun create(request: RouteRequestDto): RouteResponseDto? =
        remoteExecutor.execute("create route '${request.id}'", ioDispatcher) {
            client.from(TableName.ROUTES).insert(request)
            fetchById(request.id)
        }

    override suspend fun update(id: String, request: RouteRequestDto): RouteResponseDto? =
        remoteExecutor.execute("update route '$id'", ioDispatcher) {
            client.from(TableName.ROUTES).update(request) { filter { eq("id", id) } }
            fetchById(id)
        }

    override suspend fun delete(id: String): Boolean =
        remoteExecutor.execute("delete route '$id'", ioDispatcher) {
            client.from(TableName.ROUTES).delete { filter { eq("id", id) } }
            true
        }
}