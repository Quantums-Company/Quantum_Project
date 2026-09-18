package org.bytebloom.domain.validator

import org.bytebloom.domain.model.Warehouse

data class RouteUpdateInput(
    val id: String,
    val distanceKm: Double? = null,
    val typicalDelayMin: Int? = null,
    val originWarehouse: Warehouse? = null,
    val destinationWarehouse: Warehouse? = null
)