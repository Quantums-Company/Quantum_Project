package org.bytebloom.domain.validator

data class WarehouseUpdateInput(
    val id: String,
    val name: String? = null,
    val regionalZone: String? = null,
    val longitude: Double? = null,
    val latitude: Double? = null
)