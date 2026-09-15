package org.bytebloom.data.remote.dto.vehicleDto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable

data class VehicleRequestDto(
    @SerialName("id")
    val id: String = "",
    @SerialName("currentWarehouseId")
    val currentWarehouseId: String = "",
    @SerialName("maxCapacityKg")
    val maxCapacityKg: Double = 0.0,
    @SerialName("costPerKm")
    val costPerKm: Double = 0.0
)