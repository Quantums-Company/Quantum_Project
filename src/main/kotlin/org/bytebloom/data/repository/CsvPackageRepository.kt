package org.bytebloom.data.repository

import org.bytebloom.data.mapper.PackageMapper
import org.bytebloom.data.mapper.WarehouseReferenceMapper
import org.bytebloom.data.source.PackageDataSource
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.util.Logger

class CsvPackageRepository(
    private val warehousesById: Map<String, Warehouse>,
    private val csvPackageDataSource: PackageDataSource
) : PackageRepository {
    private var cachedPackages = listOf<Package>()

    private fun loadAll(): List<Package> {
        val packageMapper = PackageMapper(WarehouseReferenceMapper(warehousesById))
        val packageRaws = csvPackageDataSource.loadAll()

        return packageMapper.toDomain(packageRaws)
    }

    fun refresh() {
        cachedPackages = loadAll()
    }

    init {
        Logger.info("Loading packages in init...")
        cachedPackages = loadAll()
    }

    override fun getAll(): List<Package> = cachedPackages
    override fun getById(id: String): Package? {
        TODO("Not yet implemented")
    }

    override fun create(pkg: Package): Package {
        TODO("Not yet implemented")
    }

    override fun update(pkg: Package): Package {
        TODO("Not yet implemented")
    }

    override fun delete(id: String): Boolean {
        TODO("Not yet implemented")
    }
}