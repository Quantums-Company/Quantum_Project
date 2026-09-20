package org.bytebloom.data.remote.dto.packageDto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PackageRequestDto(
    @SerialName("id")
    val id: String = "",
    @SerialName("weight")
    val weight: Double = 0.0,
    @SerialName("origin_warehouse_id")
    val originWarehouseId: String = "",
    @SerialName("destination_warehouse_id")
    val destinationWarehouseId: String = "",
    @SerialName("priority")
    val priority: String = "",
    @SerialName("updated_at")
    val updatedAt: String = ""
)