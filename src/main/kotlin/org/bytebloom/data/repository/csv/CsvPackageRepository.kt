package org.bytebloom.data.repository.csv

import org.bytebloom.data.mapper.PackageMapper
import org.bytebloom.data.mapper.WarehouseReferenceMapper
import org.bytebloom.data.source.csv.PackageDataSource
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.PackageRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class CsvPackageRepository(
    private val warehousesById: Map<String, Warehouse>,
    private val csvPackageDataSource: PackageDataSource
) : PackageRepository {
    private var cachedPackages: List<Package>? = null
    private val mutex = Mutex()

    private suspend fun loadAll(): List<Package> {
        val mapper = PackageMapper(WarehouseReferenceMapper(warehousesById))

        return mapper.toDomain(csvPackageDataSource.loadAll())
    }

    suspend fun refresh() {
        mutex.withLock { cachedPackages = loadAll() }
    }

    override suspend fun getAll(): List<Package> =
        cachedPackages ?: mutex.withLock { cachedPackages ?: loadAll().also { cachedPackages = it } }

    override suspend fun getById(id: String): Package? {
        TODO("Not yet implemented")
    }

    override suspend fun create(pkg: Package): Package {
        TODO("Not yet implemented")
    }

    override suspend fun update(pkg: Package): Package {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: String): Boolean {
        TODO("Not yet implemented")
    }
}