package org.bytebloom.data.remote.dto

data class VehicleDto(
    val id: String = "",
    val currentWarehouseId: String = "",
    val maxCapacityKg: Double = 0.0,
    val costPerKm: Double = 0.0
)