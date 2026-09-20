package org.bytebloom.data.remote.dto.warehouseDto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WarehouseResponseDto(
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
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)