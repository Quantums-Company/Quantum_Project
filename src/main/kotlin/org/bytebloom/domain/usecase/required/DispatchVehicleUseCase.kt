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

        if (!canDispatch(pkg, vehicle, warehouse)) {
            return false
        }

        warehouse.cargoQueue.remove(pkg)
        warehouse.stationedVehicles.remove(vehicle)

        return true
    }

    private fun canDispatch(
        pkg: Package,
        vehicle: Vehicle,
        warehouse: Warehouse
    ): Boolean =
        pkg in warehouse.cargoQueue &&
                vehicle in warehouse.stationedVehicles &&
                pkg.weight <= vehicle.maxCapacityKg
}