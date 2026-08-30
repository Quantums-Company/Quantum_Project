package org.bytebloom.domain.usecase

import org.bytebloom.domain.model.DomainGraph
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.vehicleReshuffling.ConsistentHashingRing

private const val MIN_HUB_VEHICLES = 2

data class CargoRecoveryReport(
    val failedVehicleId: String,
    val rescueVehicleByPackageId: Map<String, String>
)

class RecoverCargoAfterVehicleFailureUseCase(
    private val graph: DomainGraph
) {

    operator fun invoke(failedVehicleId: String): CargoRecoveryReport? =
        graph.vehicles
            .firstOrNull { it.id.equals(failedVehicleId, ignoreCase = true) }
            ?.let(::recover)

    private fun recover(failed: Vehicle): CargoRecoveryReport? {
        val hubId = failed.currentWarehouse.id
        val hubVehicles = graph.vehicles.filter { it.currentWarehouse.id == hubId }
        val hubPackages = graph.packages.filter { it.originWarehouse.id == hubId }
        if (hubVehicles.size < MIN_HUB_VEHICLES || hubPackages.isEmpty()) return null

        val ring = ConsistentHashingRing(hubPackages, hubVehicles)
        val slot = ring.vehicleRing.entries
            .firstOrNull { it.value.id.equals(failed.id, ignoreCase = true) }
            ?.key
        val orphanedIds = ring.assignments[failed].orEmpty().map { it.id }

        return slot?.let {
            ring.removeVehicle(it)
            CargoRecoveryReport(
                failedVehicleId = failed.id,
                rescueVehicleByPackageId = orphanedIds.associateWith { packageId ->
                    ring.assignments.entries
                        .first { (_, cargo) -> cargo.any { it.id == packageId } }
                        .key.id
                }
            )
        }
    }
}