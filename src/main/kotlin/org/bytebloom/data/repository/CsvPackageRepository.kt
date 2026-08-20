package org.bytebloom.data.repository

import org.bytebloom.data.csv.DEFAULT_CSV_DIRECTORY
import org.bytebloom.data.csv.loadPackages
import org.bytebloom.data.mapper.PackageMapper
import org.bytebloom.data.mapper.WarehouseReferenceMapper
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.PackageRepository

class CsvPackageRepository(
    private val warehousesById: Map<String, Warehouse>,
    private val csvDirectory: String = DEFAULT_CSV_DIRECTORY
) : PackageRepository {

    override fun getAll(): List<Package> {
        val packageMapper = PackageMapper(WarehouseReferenceMapper(warehousesById))

            return packageMapper.toDomain(loadPackages(csvDirectory))
    }
}