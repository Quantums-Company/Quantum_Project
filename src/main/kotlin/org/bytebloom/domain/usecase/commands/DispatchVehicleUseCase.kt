package org.bytebloom.domain.usecase.commands

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse

class DispatchVehicleUseCase {

    operator fun invoke(
        packages: List<Package>,
        vehicle: Vehicle,
        warehouse: Warehouse
    ): Boolean {
        var packagesWeight = 0.0
        packages.forEach { pkg ->
            packagesWeight += pkg.weight
            if (!warehouse.containsPackage(pkg)) return false
        }

        if (!warehouse.hasVehicle(vehicle)) return false

        if (!vehicle.canCarryWeight(packagesWeight)) {
            return false
        }

        packages.forEach { pkg ->
            warehouse.removePackage(pkg)
        }
        warehouse.removeVehicle(vehicle)

        return true
    }
}