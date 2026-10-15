package org.bytebloom.data.repository.offline

import org.bytebloom.data.offline.CsvSnapshotStore
import org.bytebloom.data.offline.OfflineReadExecutor
import org.bytebloom.data.repository.csv.CsvWarehouseRepository
import org.bytebloom.data.repository.remote.RemoteWarehouseRepository
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.WarehouseRepository

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