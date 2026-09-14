package org.bytebloom.data.remote.dto

data class RouteDto(
    val id: String = "",
    val originWarehouseId: String = "",
    val destinationWarehouseId: String = "",
    val distanceKm: Double = 0.0,
    val typicalDelayMin: Int = 0
)