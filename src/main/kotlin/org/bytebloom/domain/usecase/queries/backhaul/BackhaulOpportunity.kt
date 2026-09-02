package org.bytebloom.domain.usecase.queries.backhaul

import org.bytebloom.domain.model.Package

data class BackhaulOpportunity(
    val vehicleId: String,
    val outboundWarehouseId: String,
    val returnWarehouseId: String,
    val packages: List<Package>,
    val totalCargoWeightKg: Double,
    val remainingCapacityKg: Double
)