package org.bytebloom.data.remote.dto

import kotlinx.serialization.Serializable
@Serializable

data class WarehouseDto(
    val id: String = "",
    val name: String = "",
    val regionalZone: String = "",
    val longitude: Double = 0.0,
    val latitude: Double = 0.0
)