package org.bytebloom.logiroute.data.repository.offline

import org.bytebloom.logiroute.data.csv.cache.CsvSnapshotStore
import org.bytebloom.logiroute.data.offline.OfflineReadExecutor
import org.bytebloom.logiroute.data.csv.repository.CsvPackageRepository
import org.bytebloom.logiroute.data.repository.remote.RemotePackageRepository
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.repository.PackageRepository

class OfflineFirstPackageRepository(
    private val remote: RemotePackageRepository,
    private val local: CsvPackageRepository,
    private val snapshots: CsvSnapshotStore,
    private val executor: OfflineReadExecutor
) : PackageRepository {

    override suspend fun getAll(): List<Package> =
        executor.read(
            remoteCall = { remote.getAll() },
            localCall = { local.getAll() },
            saveSnapshot = { remotePackages ->
                val localPackages = local.getAll()

                if (remotePackages != localPackages) {
                    snapshots.savePackages(remotePackages)
                    local.refresh()
                }
            }
        )

    override suspend fun getById(id: String): Package? =
        getAll().find { it.id == id }

    override suspend fun create(pkg: Package): Package =
        remote.create(pkg)

    override suspend fun update(pkg: Package): Package =
        remote.update(pkg)

    override suspend fun delete(id: String): Boolean =
        remote.delete(id)
}