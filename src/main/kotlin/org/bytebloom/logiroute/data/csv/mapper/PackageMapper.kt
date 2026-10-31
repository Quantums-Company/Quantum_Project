package org.bytebloom.logiroute.data.csv.mapper

import org.bytebloom.logiroute.data.csv.raw.PackageRaw
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Warehouse

class PackageMapper(
    private val warehouseMapper: WarehouseReferenceMapper
) {

    private fun PackageRaw.toDomain(
        originWarehouse: Warehouse,
        destinationWarehouse: Warehouse
    ): Package {
        val mappedPackage = Package(
            id = id,
            weight = weight,
            priority = priority,
            originWarehouse = originWarehouse,
            destinationWarehouse = destinationWarehouse
        )

        mappedPackage.originWarehouse.addPackage(mappedPackage)
        return mappedPackage
    }

    private fun mapPackage(packageRaw: PackageRaw): Package? {

        val originWarehouse = warehouseMapper.map(
            packageRaw.originWarehouseId
        ) ?: return null

        val destinationWarehouse = warehouseMapper.map(
            packageRaw.destinationWarehouseId
        ) ?: return null

        return packageRaw.toDomain(originWarehouse, destinationWarehouse)
    }
    fun toDomain(rawPackages: List<PackageRaw>): List<Package> {
        return rawPackages.mapNotNull(::mapPackage)
    }
}

