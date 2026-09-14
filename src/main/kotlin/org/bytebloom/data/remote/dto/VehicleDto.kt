package org.bytebloom.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable

data class VehicleDto(
    @SerialName("id")
    val id: String = "",
    @SerialName("currentWarehouseId")
    val currentWarehouseId: String = "",
    @SerialName("maxCapacityKg")
    val maxCapacityKg: Double = 0.0,
    @SerialName("costPerKm")
    val costPerKm: Double = 0.0
)