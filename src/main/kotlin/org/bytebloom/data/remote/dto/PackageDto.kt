package org.bytebloom.data.remote.dto

data class PackageDto(
    val id: String = "",
    val weight: Double = 0.0,
    val originWarehouseId: String = "",
    val destinationWarehouseId: String = "",
    val priority: String = ""
)