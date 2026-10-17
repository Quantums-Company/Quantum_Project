package org.bytebloom.logiroute.data.repository.offline

import org.bytebloom.logiroute.data.csv.cache.CsvSnapshotStore
import org.bytebloom.logiroute.data.offline.OfflineReadExecutor
import org.bytebloom.logiroute.data.csv.repository.CsvVehicleRepository
import org.bytebloom.logiroute.data.remote.repository.RemoteVehicleRepository
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.repository.VehicleRepository

class OfflineFirstVehicleRepository(
    private val remote: RemoteVehicleRepository,
    private val local: CsvVehicleRepository,
    private val snapshots: CsvSnapshotStore,
    private val executor: OfflineReadExecutor
) : VehicleRepository {

    override suspend fun getAll(): List<Vehicle> =
        executor.read(
            remoteCall = { remote.getAll() },
            localCall = { local.getAll() },
            saveSnapshot = { remoteVehicles ->
                val localVehicles = local.getAll()

                if (remoteVehicles != localVehicles) {
                    snapshots.saveVehicles(remoteVehicles)
                    local.refresh()
                }
            }
        )

    override suspend fun getById(id: String): Vehicle? =
        getAll().find { it.id == id }

    override suspend fun create(vehicle: Vehicle): Vehicle =
        remote.create(vehicle)

    override suspend fun update(vehicle: Vehicle): Vehicle =
        remote.update(vehicle)

    override suspend fun delete(id: String): Boolean =
        remote.delete(id)
}