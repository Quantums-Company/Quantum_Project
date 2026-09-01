package org.bytebloom.domain.usecase

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.vehicleReshuffling.ConsistentHashingRing

private const val MIN_HUB_VEHICLES = 2

data class CargoRecoveryReport(
    val failedVehicleId: String,
    val rescueVehicleByPackageId: Map<String, String>
)

class RecoverCargoAfterVehicleFailureUseCase {

    operator fun invoke(
        failedVehicle: Vehicle
    ): CargoRecoveryReport? {

        val warehouse = failedVehicle.currentWarehouse
        val vehicles = warehouse.stationedVehicles
        val packages = warehouse.cargoQueue

        if (!canRecoverCargo(vehicles, packages)) {
            return null
        }

        return redistributeCargo(
            failedVehicle = failedVehicle,
            vehicles = vehicles,
            packages = packages
        )
    }

    private fun canRecoverCargo(
        vehicles: List<Vehicle>,
        packages: List<Package>
    ): Boolean =
        vehicles.size >= MIN_HUB_VEHICLES &&
                packages.isNotEmpty()

    private fun redistributeCargo(
        failedVehicle: Vehicle,
        vehicles: List<Vehicle>,
        packages: List<Package>
    ): CargoRecoveryReport? {

        val ring = ConsistentHashingRing(
            packages = packages,
            vehicles = vehicles
        )

        val failedSlot =
            ring.findSlotForVehicle(failedVehicle.id)
                ?: return null

        val orphanedPackages =
            ring.getAssignedPackages(failedVehicle)

        ring.removeVehicle(failedSlot)

        val assignments =
            orphanedPackages.associate { pkg ->
                pkg.id to ring.findVehicleForPackage(pkg.id).id
            }

        return CargoRecoveryReport(
            failedVehicleId = failedVehicle.id,
            rescueVehicleByPackageId = assignments
        )
    }
}