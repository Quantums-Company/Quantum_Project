package org.bytebloom.logiroute.domain.usecase.commands

import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse

class AddVehicleToHubUseCase {
    operator fun invoke(warehouse: Warehouse, vehicle: Vehicle): Vehicle {
        vehicle.currentWarehouse.removeVehicleById(vehicle.id)
        val stationedVehicle = vehicle.reassignedTo(warehouse)
        warehouse.addVehicle(stationedVehicle)
        return stationedVehicle
    }
}