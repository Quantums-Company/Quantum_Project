package org.bytebloom.data.repository

import org.bytebloom.data.csv.loadPackages
import org.bytebloom.data.mapper.toDomain
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.PackageRepository

class CsvPackageRepository(
    private val warehouseMap: Map<String, Warehouse>
) : PackageRepository {
    override fun getAllPackages(): List<Package> {
        return loadPackages().mapNotNull { it.toDomain(warehouseMap) }
    }
}