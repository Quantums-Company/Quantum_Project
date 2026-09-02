package org.bytebloom.domain.model

class Vehicle(
    val id: String,
    val maxCapacityKg: Double,
    val costPerKm: Double,
    val currentWarehouse: Warehouse,
    val cargo: List<Package>,
    val available: Boolean
)