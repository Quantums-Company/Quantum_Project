package org.bytebloom.domain.usecase.queries.planing

import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.vehicleReshuffling.ConsistentHashingRing

private const val MIN_HUB_VEHICLES = 2

class FindCargoRecoveryPlanUseCase {

    operator fun invoke(
        failedVehicle: Vehicle
    ): CargoRecoveryPlan? {

        val warehouse = failedVehicle.currentWarehouse
        val vehicles = warehouse.stationedVehicles
        val packages = warehouse.cargoQueue

        val availableVehicles = vehicles.filterNot {
            it.id.equals(failedVehicle.id, ignoreCase = true)
        }

        if (packages.isEmpty() || availableVehicles.isEmpty()) {
            return null
        }

        val ring = ConsistentHashingRing(
            packages = packages,
            vehicles = vehicles
        )

        return ring.createRecoveryPlan(failedVehicle)
    }
}