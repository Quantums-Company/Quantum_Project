package org.bytebloom.data.remote.dto.routeDto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable

data class RouteRequestDto (
    @SerialName("id")
    val id: String = "",
    @SerialName("originWarehouseId")
    val originWarehouseId: String = "",
    @SerialName("destinationWarehouseId")
    val destinationWarehouseId: String = "",
    @SerialName("distanceKm")
    val distanceKm: Double = 0.0,
    @SerialName("typicalDelayMin")
    val typicalDelayMin: Int = 0
)