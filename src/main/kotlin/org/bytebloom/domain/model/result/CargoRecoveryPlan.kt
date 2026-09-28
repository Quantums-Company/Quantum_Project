package org.bytebloom.domain.model.result

data class CargoRecoveryPlan(
    val failedVehicleId: String,
    val rescueVehicleByPackageId: Map<String, String>
)