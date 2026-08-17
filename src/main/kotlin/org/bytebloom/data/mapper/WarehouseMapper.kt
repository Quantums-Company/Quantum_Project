package org.bytebloom.data.mapper

import org.bytebloom.data.raw.WarehouseRaw
import org.bytebloom.domain.model.Warehouse

fun WarehouseRaw.toDomain(): Warehouse {
    return Warehouse(
        id = id,
        name = name,
        regionalZone = regionalZone,
        longitude = longitude,
        latitude = latitude
    )
}

