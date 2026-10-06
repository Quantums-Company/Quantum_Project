package org.bytebloom.domain.model.input.update

import org.bytebloom.domain.model.Warehouse

data class VehicleUpdateInput(
    val id: String,
    val maxCapacityKg: Double? = null,
    val costPerKm: Double? = null,
    val currentWarehouse: Warehouse? = null
) {
    fun hasUpdates(): Boolean {
        return maxCapacityKg != null ||
                costPerKm != null ||
                currentWarehouse != null
    }
}