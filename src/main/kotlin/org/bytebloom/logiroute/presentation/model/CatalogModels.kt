package org.bytebloom.logiroute.presentation.model

enum class PriorityUi {
    URGENT,
    STANDARD,
    LOW
}

enum class RegionUi {
    WEST,
    EAST,
    NORTH,
    SOUTH,
    CENTRAL
}

data class WarehouseUi(
    val id: String,
    val name: String,
    val zone: String,
    val latitude: Double,
    val longitude: Double
)

data class VehicleUi(
    val id: String,
    val capacityKg: Double,
    val costPerKm: Double,
    val warehouseName: String
)

data class PackageUi(
    val id: String,
    val weightKg: Double,
    val priority: PriorityUi,
    val originName: String,
    val destinationName: String
)

data class RouteUi(
    val id: String,
    val originName: String,
    val destinationName: String,
    val distanceKm: Double,
    val typicalDelayMin: Int
)
