package org.bytebloom.logiroute.data.repository.offline

import org.bytebloom.logiroute.data.csv.cache.CsvSnapshotStore
import org.bytebloom.logiroute.data.offline.OfflineReadExecutor
import org.bytebloom.logiroute.data.csv.repository.CsvWarehouseRepository
import org.bytebloom.logiroute.data.remote.repository.RemoteWarehouseRepository
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.repository.WarehouseRepository

class OfflineFirstWarehouseRepository(
    private val remote: RemoteWarehouseRepository,
    private val local: CsvWarehouseRepository,
    private val snapshots: CsvSnapshotStore,
    private val executor: OfflineReadExecutor
) : WarehouseRepository {

    override suspend fun getAll(): List<Warehouse> =
        executor.read(
            remoteCall = { remote.getAll() },
            localCall = { local.getAll() },
            saveSnapshot = { remoteWarehouses ->
                val localWarehouses = local.getAll()

                if (remoteWarehouses != localWarehouses) {
                    snapshots.saveWarehouses(remoteWarehouses)
                    local.refresh()
                }
            }
        )

    override suspend fun getById(id: String): Warehouse? =
        getAll().find { it.id == id }

    override suspend fun create(warehouse: Warehouse): Warehouse =
        remote.create(warehouse)

    override suspend fun update(warehouse: Warehouse): Warehouse =
        remote.update(warehouse)

    override suspend fun delete(id: String): Boolean =
        remote.delete(id)
}