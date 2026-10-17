package org.bytebloom.logiroute.data.raw

import org.bytebloom.logiroute.domain.model.Priority

data class PackageRaw(
    val id: String,
    val weight: Double,
    val destinationWarehouseId: String,
    val originWarehouseId: String,
    val priority: Priority
)