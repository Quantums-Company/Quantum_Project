package org.bytebloom.logiroute.data.repository.csv

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bytebloom.logiroute.data.mapper.PackageMapper
import org.bytebloom.logiroute.data.mapper.WarehouseReferenceMapper
import org.bytebloom.logiroute.data.source.csv.PackageDataSource
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.repository.PackageRepository
import org.bytebloom.logiroute.domain.repository.WarehouseRepository

class CsvPackageRepository(
    private val warehouseRepository: WarehouseRepository,
    private val csvPackageDataSource: PackageDataSource
) : PackageRepository {

    private var cachedPackages: List<Package>? = null
    private val mutex = Mutex()

    private suspend fun loadAll(): List<Package> {
        val warehousesById = warehouseRepository
            .getAll()
            .associateBy { it.id }

        val mapper = PackageMapper(
            WarehouseReferenceMapper(warehousesById)
        )

        return mapper.toDomain(csvPackageDataSource.loadAll())
    }

    suspend fun refresh(): List<Package> =
        mutex.withLock {
            loadAll().also { packages ->
                cachedPackages = packages
            }
        }

    override suspend fun getAll(): List<Package> {
        cachedPackages?.let { return it }

        return mutex.withLock {
            cachedPackages
                ?: loadAll().also { packages ->
                    cachedPackages = packages
                }
        }
    }

    override suspend fun getById(id: String): Package? =
        getAll().find { it.id == id }

    override suspend fun create(pkg: Package): Package =
        throw UnsupportedOperationException(
            "CSV repository is read-only. Create the package through the remote repository."
        )

    override suspend fun update(pkg: Package): Package =
        throw UnsupportedOperationException(
            "CSV repository is read-only. Update the package through the remote repository."
        )

    override suspend fun delete(id: String): Boolean =
        throw UnsupportedOperationException(
            "CSV repository is read-only. Delete the package through the remote repository."
        )
}