package org.bytebloom.data.raw

data class PackageRaw(
    val id: String,
    val weight: Double,
    val destinationWarehouseId: String,
    val originWarehouseId: String,
    val priority: Priority
)