package org.bytebloom.domain.validator

import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Warehouse

data class PackageUpdateInput(
    val id: String,
    val weight: Double? = null,
    val priority: Priority? = null,
    val originWarehouse: Warehouse? = null,
    val destinationWarehouse: Warehouse? = null
)