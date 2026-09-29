package org.bytebloom.domain.model.updateInput

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