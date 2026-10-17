package org.bytebloom.logiroute.data.raw

data class VehicleRaw(
    val id: String,
    val currentWarehouseId: String,
    val maxCapacityKg: Double,
    val costPerKm: Double
)