package org.bytebloom.domain.usecase.queries.planing

data class CargoRecoveryPlan(
    val failedVehicleId: String,
    val rescueVehicleByPackageId: Map<String, String>
)