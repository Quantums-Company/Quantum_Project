package org.bytebloom.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable

data class PackageDto(
    @SerialName("id")
    val id: String = "",
    @SerialName("weight")
    val weight: Double = 0.0,
    @SerialName("originWarehouseId")
    val originWarehouseId: String = "",
    @SerialName("destinationWarehouseId")
    val destinationWarehouseId: String = "",
    @SerialName("priority")
    val priority: String = ""
)