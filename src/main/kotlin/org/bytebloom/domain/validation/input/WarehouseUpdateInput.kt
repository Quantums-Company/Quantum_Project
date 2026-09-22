package org.bytebloom.domain.validation.input

data class WarehouseUpdateInput(
    val id: String,
    val name: String? = null,
    val regionalZone: String? = null,
    val longitude: Double? = null,
    val latitude: Double? = null
) {
    fun hasUpdates(): Boolean {
        return name != null ||
                regionalZone != null ||
                longitude != null ||
                latitude != null
    }
}