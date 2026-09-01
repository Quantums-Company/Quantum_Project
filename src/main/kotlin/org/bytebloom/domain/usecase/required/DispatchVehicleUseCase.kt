package org.bytebloom.domain.usecase.required

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse

class DispatchVehicleUseCase {

    operator fun invoke(
        pkg: Package,
        vehicle: Vehicle,
        warehouse: Warehouse
    ): Boolean {

        if (pkg !in warehouse.cargoQueue) {
            return false
        }

        if (vehicle !in warehouse.stationedVehicles) {
            return false
        }

        if (pkg.weight > vehicle.maxCapacityKg) {
            return false
        }

        warehouse.removePackage(pkg)
        warehouse.removeVehicle(vehicle)

        return true
    }
}