package org.bytebloom.domain.usecase.required

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse


class DispatchVehicleUseCase {
    fun theAvailableVehcles(vehicle: Vehicle, warehouse: Warehouse): List<Vehicle>{
        val availableVehicles = warehouse.stationedVehicles.filter { it.id != vehicle.id }
        return availableVehicles
    }
    operator fun invoke(pkg: Package, availableVehicles: List<Vehicle>): List<Vehicle> {
        val available = availableVehicles.filter { pkg.weight <= it.maxCapacityKg }//.filter {  }
        return available
    }
}
