package org.bytebloom.domain.model

class Vehicle(
    val id: String,
    val maxCapacityKg: Double,
    val costPerKm: Double,
    val currentWarehouse: Warehouse
)
//{
//    override fun toString(): String {
//        return "Vehicle(id='$id', maxCapacityKg=$maxCapacityKg, costPerKm=$costPerKm, currentHub:${currentHub.id})"
//    }
//}