package org.bytebloom.data.mapper

import org.bytebloom.data.raw.PackageRaw
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Warehouse

class PackageMapper(
    private val warehouseMapper: WarehouseReferenceMapper
) {

    private fun PackageRaw.toDomain(
        originWarehouse: Warehouse,
        destinationWarehouse: Warehouse
    ): Package {
        return Package(
            id = id,
            weight = weight,
            priority = priority,
            originWarehouse = originWarehouse,
            destinationWarehouse = destinationWarehouse
        )
    }

    private fun map(raw: PackageRaw): Package? {

        val origin = warehouseMapper.map(
            raw.originWarehouseId,
            "Package",
            raw.id
        ) ?: return null

        val destination = warehouseMapper.map(
            raw.destinationWarehouseId,
            "Package",
            raw.id
        ) ?: return null

        return raw.toDomain(origin, destination)
    }
    fun toDomain(packageRaws: List<PackageRaw>): List<Package> {
        return packageRaws.mapNotNull(::map)
    }
}

