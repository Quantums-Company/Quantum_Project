package org.bytebloom.data.mapper

import org.bytebloom.data.raw.PackageRaw
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Warehouse

fun PackageRaw.toDomain(
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