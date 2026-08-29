package org.bytebloom.data.repository

import org.bytebloom.data.csv.DEFAULT_CSV_DIRECTORY
import org.bytebloom.data.csv.loadPackages
import org.bytebloom.data.mapper.PackageMapper
import org.bytebloom.data.mapper.WarehouseReferenceMapper
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.util.Logger

class CsvPackageRepository(
    private val warehousesById: Map<String, Warehouse>,
    private val csvDirectory: String = DEFAULT_CSV_DIRECTORY
) : PackageRepository {
    private var achedPackages= listOf<Package>()

    private fun loadAll():List<Package>{
        val packageMapper = PackageMapper(WarehouseReferenceMapper(warehousesById))
        val packageRaws = loadPackages(csvDirectory)

        return packageMapper.toDomain(packageRaws)
    }

    fun refresh(){
        achedPackages = loadAll()
    }

    init {
        Logger.info("Loading packages in init...")
        achedPackages = loadAll()
    }
    override fun getAll(): List<Package>  = achedPackages
}