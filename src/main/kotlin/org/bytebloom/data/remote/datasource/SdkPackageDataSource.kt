package org.bytebloom.data.remote.datasource

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.bytebloom.data.remote.dto.packageDto.PackageRequestDto
import org.bytebloom.data.remote.dto.packageDto.PackageResponseDto
import org.bytebloom.data.remote.util.RemoteExecutor
import org.bytebloom.data.source.remote.PackageRemoteDataSource

class SdkPackageDataSource(
    private val client: SupabaseClient,
    private val remoteExecutor: RemoteExecutor,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PackageRemoteDataSource {

    override suspend fun loadAll(): List<PackageResponseDto> =
        remoteExecutor.execute("getAll packages", ioDispatcher) {
            client.from(TableName.PACKAGES).select().decodeList<PackageResponseDto>()
        }

    override suspend fun loadById(id: String): PackageResponseDto? =
        remoteExecutor.execute("getById package '$id'", ioDispatcher) {
            client.from(TableName.PACKAGES).select { filter { eq("id", id) } }.
            decodeSingleOrNull<PackageResponseDto>()
        }

    override suspend fun create(request: PackageRequestDto): PackageResponseDto? =
        remoteExecutor.execute("create package '${request.id}'", ioDispatcher) {
            client.from(TableName.PACKAGES).insert(request)
            loadById(request.id)
        }

    override suspend fun update(id: String, request: PackageRequestDto): PackageResponseDto? =
        remoteExecutor.execute("update package '$id'", ioDispatcher) {
            client.from(TableName.PACKAGES).update(request) { filter { eq("id", id) } }
            loadById(id)
        }

    override suspend fun delete(id: String): Boolean =
        remoteExecutor.execute("delete package '$id'", ioDispatcher) {
            client.from(TableName.PACKAGES).delete { filter { eq("id", id) } }
            true
        }
}