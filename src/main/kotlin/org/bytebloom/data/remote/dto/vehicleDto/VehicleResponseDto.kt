package org.bytebloom.data.remote.dto.vehicleDto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VehicleResponseDto(
    @SerialName("id")
    val id: String = "",
    @SerialName("current_warehouse_id")
    val currentWarehouseId: String = "",
    @SerialName("max_capacity_kg")
    val maxCapacityKg: Double = 0.0,
    @SerialName("cost_per_km")
    val costPerKm: Double = 0.0,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)