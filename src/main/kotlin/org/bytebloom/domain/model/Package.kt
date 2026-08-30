package org.bytebloom.domain.model

class Package(
    val id: String,
    val weight: Double,
    val priority: Priority,
    val originWarehouse: Warehouse,
    var destinationWarehouse: Warehouse
)
