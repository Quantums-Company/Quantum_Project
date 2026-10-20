package org.bytebloom.logiroute.domain.usecase.dispatch

import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.result.CargoRecoveryPlan
import org.bytebloom.logiroute.domain.reshuffling.ConsistentHashingRing

class FindCargoRecoveryPlanUseCase {

    operator fun invoke(
        failedVehicle: Vehicle
    ): CargoRecoveryPlan? {

        val warehouse = failedVehicle.currentWarehouse
        val vehicles = warehouse.stationedVehicles
        val packages = warehouse.cargoQueue

        val healthyVehicles = vehicles.filterNot {
            it.id.equals(failedVehicle.id, ignoreCase = true)
        }

        if (packages.isEmpty() || healthyVehicles.isEmpty()) return null

        val ring = ConsistentHashingRing(
            packages = packages,
            vehicles = vehicles
        )

        return ring.createRecoveryPlan(failedVehicle)
    }
}