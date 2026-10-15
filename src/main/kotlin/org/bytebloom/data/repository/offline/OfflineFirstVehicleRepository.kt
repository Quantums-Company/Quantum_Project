package org.bytebloom.data.repository.offline

import org.bytebloom.data.offline.CsvSnapshotStore
import org.bytebloom.data.offline.OfflineReadExecutor
import org.bytebloom.data.repository.csv.CsvVehicleRepository
import org.bytebloom.data.repository.remote.RemoteVehicleRepository
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.repository.VehicleRepository

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