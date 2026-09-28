package org.bytebloom.domain.validation.input

import org.bytebloom.domain.model.Warehouse

data class RouteUpdateInput(
    val id: String,
    val distanceKm: Double? = null,
    val typicalDelayMin: Int? = null,
    val originWarehouse: Warehouse? = null,
    val destinationWarehouse: Warehouse? = null
) {
    fun hasUpdates(): Boolean {
        return distanceKm != null ||
                typicalDelayMin != null ||
                originWarehouse != null ||
                destinationWarehouse != null
    }
}