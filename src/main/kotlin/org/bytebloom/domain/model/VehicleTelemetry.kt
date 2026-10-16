package org.bytebloom.domain.model

data class VehicleTelemetry(
    val vehicleId: String,
    val latitude: Double,
    val longitude: Double,
    val speedKph: Double,
    val fuelPercent: Double
)