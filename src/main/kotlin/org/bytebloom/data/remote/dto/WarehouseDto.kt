package org.bytebloom.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable

data class WarehouseDto(
    @SerialName("id")
    val id: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("regionalZone")
    val regionalZone: String = "",
    @SerialName("longitude")
    val longitude: Double = 0.0,
    @SerialName("latitude")
    val latitude: Double = 0.0
)