package org.bytebloom.data.remote.dto.PackageDto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable

data class PackageResponseDto(
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