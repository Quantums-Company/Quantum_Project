package org.bytebloom.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable

data class RouteDto(
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