package org.bytebloom.logiroute.data.repository.offline

import org.bytebloom.logiroute.data.offline.CsvSnapshotStore
import org.bytebloom.logiroute.data.offline.OfflineReadExecutor
import org.bytebloom.logiroute.data.repository.csv.CsvRouteRepository
import org.bytebloom.logiroute.data.repository.remote.RemoteRouteRepository
import org.bytebloom.logiroute.domain.model.Route
import org.bytebloom.logiroute.domain.repository.RouteRepository

class OfflineFirstRouteRepository(
    private val remote: RemoteRouteRepository,
    private val local: CsvRouteRepository,
    private val snapshots: CsvSnapshotStore,
    private val executor: OfflineReadExecutor
) : RouteRepository {

    override suspend fun getAll(): List<Route> =
        executor.read(
            remoteCall = { remote.getAll() },
            localCall = { local.getAll() },
            saveSnapshot = { remoteRoutes ->
                val localRoutes = local.getAll()

                if (remoteRoutes != localRoutes) {
                    snapshots.saveRoutes(remoteRoutes)
                    local.refresh()
                }
            }
        )

    override suspend fun getById(id: String): Route? =
        getAll().find { it.id == id }

    override suspend fun create(route: Route): Route =
        remote.create(route)

    override suspend fun update(route: Route): Route =
        remote.update(route)

    override suspend fun delete(id: String): Boolean =
        remote.delete(id)
}