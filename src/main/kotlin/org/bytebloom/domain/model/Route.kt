package org.bytebloom.domain.model

class Route(
    val id: String,
    val distanceKm: Double,
    val typicalDelayMin: Int,
    val originWarehouse: Warehouse,
    val destinationWarehouse: Warehouse
)
//{
//    override fun toString(): String {
//        return "Route(id='$id', distanceKm=$distanceKm, typicalDelayMin=$typicalDelayMin, origin:${origin.id}, destination=${destination.id})"
//    }
//}
