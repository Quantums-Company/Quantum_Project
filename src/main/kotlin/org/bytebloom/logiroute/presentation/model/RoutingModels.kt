package org.bytebloom.logiroute.presentation.model

data class RouteLegUi(
    val fromName: String,
    val toName: String,
    val distanceKm: Double
)

data class RoutePlanUi(
    val stops: List<WarehouseUi>,
    val legs: List<RouteLegUi>,
    val totalDistanceKm: Double
) {
    val hops: Int get() = legs.size
}

data class ClassificationUi(
    val packageId: String,
    val region: RegionUi
)
