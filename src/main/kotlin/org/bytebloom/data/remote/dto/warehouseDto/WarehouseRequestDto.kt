package org.bytebloom.data.remote.dto.warehouseDto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WarehouseRequestDto(
    @SerialName("id")
    val id: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("regional_zone")
    val regionalZone: String = "",
    @SerialName("longitude")
    val longitude: Double = 0.0,
    @SerialName("latitude")
    val latitude: Double = 0.0,
    @SerialName("updated_at")
    val updatedAt: String = ""
)