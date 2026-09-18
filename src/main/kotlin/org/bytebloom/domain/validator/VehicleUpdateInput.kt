package org.bytebloom.domain.validator

import org.bytebloom.domain.model.Warehouse

data class VehicleUpdateInput(
    val id: String,
    val maxCapacityKg: Double? = null,
    val costPerKm: Double? = null,
    val currentWarehouse: Warehouse? = null
)