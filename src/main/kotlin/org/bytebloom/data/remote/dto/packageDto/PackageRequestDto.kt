package org.bytebloom.data.remote.dto.packageDto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable

data class PackageRequestDto(
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