package org.bytebloom.domain.model

data class ShipmentEstimate(
    val packageId: String,
    val route: List<String>,
    val totalDistanceKm: Double,
    val estimatedMinutes: Int
)