package org.bytebloom.domain.validation.input

import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Warehouse

data class PackageUpdateInput(
    val id: String,
    val weight: Double? = null,
    val priority: Priority? = null,
    val originWarehouse: Warehouse? = null,
    val destinationWarehouse: Warehouse? = null
) {
    fun hasUpdates(): Boolean {
        return weight != null ||
                priority != null ||
                originWarehouse != null ||
                destinationWarehouse != null
    }
}