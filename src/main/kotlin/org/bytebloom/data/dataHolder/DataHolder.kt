package org.bytebloom.data.dataHolder

data class PackageRaw(
    val id: String,
    val weight: Double,
    val destinationWarehouseId: String,
    val originWarehouseId: String,
    val priority: Priority
)

enum class Priority {
    URGENT,
    STANDARD,
    LOW
}

data class VehicleRaw(
    val id: String,
    val currentWarehouseId: String,
    val maxCapacityKg: Double,
    val costPerKm: Double
)

data class RouteRaw(
    val id: String,
    val originWarehouseId: String,
    val destinationWarehouseId: String,
    val distanceKm: Double,
    val typicalDelayMin: Int
)

data class WarehouseRaw(
    val id: String,
    val name: String,
    val regionalZone: String,
    val longitude: Double,
    val latitude: Double
)