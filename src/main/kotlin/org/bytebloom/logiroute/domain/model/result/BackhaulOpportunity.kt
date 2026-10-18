package org.bytebloom.logiroute.domain.model.result

import org.bytebloom.logiroute.domain.model.Package

data class BackhaulOpportunity(
    val vehicleId: String,
    val outboundWarehouseId: String,
    val returnWarehouseId: String,
    val packages: List<Package>,
    val totalCargoWeightKg: Double,
    val remainingCapacityKg: Double
)