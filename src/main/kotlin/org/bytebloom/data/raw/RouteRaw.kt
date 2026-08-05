package org.bytebloom.data.raw

data class RouteRaw(
    val id: String,
    val originWarehouseId: String,
    val destinationWarehouseId: String,
    val distanceKm: Double,
    val typicalDelayMin: Int
)