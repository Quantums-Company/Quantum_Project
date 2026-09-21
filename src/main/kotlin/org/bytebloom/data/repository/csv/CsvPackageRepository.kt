package org.bytebloom.data.repository.csv

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bytebloom.data.mapper.PackageMapper
import org.bytebloom.data.mapper.WarehouseReferenceMapper
import org.bytebloom.data.source.csv.PackageDataSource
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.util.Logger

class CsvPackageRepository(
    private val warehousesById: Map<String, Warehouse>,
    private val csvPackageDataSource: PackageDataSource
) : PackageRepository {
    private var cachedPackages = listOf<Package>()

    private suspend fun loadAll(): List<Package> {
        val packageMapper = PackageMapper(WarehouseReferenceMapper(warehousesById))
        val packageRaws = csvPackageDataSource.loadAll()

        return packageMapper.toDomain(packageRaws)
    }

    suspend fun refresh() {
        cachedPackages = loadAll()
    }

    init {
        Logger.info("Loading packages in init...")
        CoroutineScope(Dispatchers.IO).launch {
            refresh()
        }
    }

    override suspend fun getAll(): List<Package> = cachedPackages
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