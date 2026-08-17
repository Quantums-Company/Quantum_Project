package org.bytebloom.data.mapper



import org.bytebloom.data.raw.PackageRaw
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.data.raw.WarehouseRaw
fun PackageRaw.toDomain(warehouse: WarehouseRaw): Package? {


    return Package(
        id = id,
        weight = weight,
        priority = priority,
        originWarehouse = warehouse.toDomain(),
        destinationWarehouse = warehouse.toDomain()
    )
}