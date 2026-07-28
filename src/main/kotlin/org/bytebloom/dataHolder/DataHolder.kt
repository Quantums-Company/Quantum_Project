package org.bytebloom.dataHolder

data class packageRaw(
    val id: String,
    val weight: Double,
    val destinationHubId: String,
    val originHubId: String,
    val priority: Priority
)

enum class Priority {
    URGENT,
    STANDARD,
    LOW
}

data class vehicleRaw(
    val vehicleId: String,
    val currentHubId: String,
    val maxCapacityKg: Double,
    val costPerKm: Double
)

data class routeRaw(
    val routeId: String,
    val originHubId: String,
    val destinationHubId: String,
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