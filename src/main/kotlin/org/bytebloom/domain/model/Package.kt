package org.bytebloom.domain.model

import org.bytebloom.data.raw.Priority

class Package(
    val id: String,
    val weight: Double,
    val priority: Priority,
    val originWarehouse: Warehouse,
    val destinationWarehouse: Warehouse
)
//{
//    override fun toString(): String {
//        return "Package(id='$id', weight=$weight, priority=$priority, origin:${origin.id}, destination=${destination.id})"
//    }
//}
