package org.bytebloom.domain.model

class Vehicle(
    val id: String,
    val maxCapacityKg: Double,
    val costPerKm: Double,
    var currentWarehouse: Warehouse
){
    fun canCarryWeight(weight: Double): Boolean = weight < maxCapacityKg
}