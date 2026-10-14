package org.bytebloom.domain.model.result

data class VehicleDelayNotification(
    val vehicleId: String,
    val speedKph: Double,
    val message: String
)