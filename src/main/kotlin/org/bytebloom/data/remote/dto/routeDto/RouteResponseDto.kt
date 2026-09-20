package org.bytebloom.data.remote.dto.routeDto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RouteResponseDto(
    @SerialName("id")
    val id: String = "",
    @SerialName("origin_warehouse_id")
    val originWarehouseId: String = "",
    @SerialName("destination_warehouse_id")
    val destinationWarehouseId: String = "",
    @SerialName("distance_km")
    val distanceKm: Double = 0.0,
    @SerialName("typical_delay_min")
    val typicalDelayMin: Int = 0,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)