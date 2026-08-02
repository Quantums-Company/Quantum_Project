package org.bytebloom.domain.model.dataHolder

data class packageRaw(
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

data class vehicleRaw(
    val id: String,
    val currentHubId: String,
    val maxCapacityKg: Double,
    val costPerKm: Double
)

data class routeRaw(
    val id: String,
    val originWarehouseI: String,
    val destinationWarehouseId: String,
    val distanceKm: Double,
    val typicalDelayMin: Int
)

data class warehouseRaw(
    val id: String,
    val name: String,
    val regionalZone: String,
    val longitude: Double,
    val latitude: Double
)