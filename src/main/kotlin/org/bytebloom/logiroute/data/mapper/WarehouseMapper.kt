package org.bytebloom.logiroute.data.mapper

import org.bytebloom.logiroute.data.raw.WarehouseRaw
import org.bytebloom.logiroute.domain.model.Warehouse

fun WarehouseRaw.toDomain(): Warehouse {
    return Warehouse(
        id = id,
        name = name,
        regionalZone = regionalZone,
        longitude = longitude,
        latitude = latitude
    )
}

fun List<WarehouseRaw>.toDomain(): List<Warehouse> {
    return this.map { it.toDomain() }
}