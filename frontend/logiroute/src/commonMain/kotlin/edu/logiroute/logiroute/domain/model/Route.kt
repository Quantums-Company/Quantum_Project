package edu.logiroute.logiroute.domain.model

data class Route(
    val id: String,
    val distanceKm: Double,
    val typicalDelayMin: Int,
    val originWarehouseId: String,
    val destinationWarehouseId: String
)