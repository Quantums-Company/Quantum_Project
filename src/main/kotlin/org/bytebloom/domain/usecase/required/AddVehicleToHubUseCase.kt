package org.bytebloom.domain.usecase.required

import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse

class AddVehicleToHubUseCase {

    operator fun invoke(vehicle: Vehicle): Warehouse {
        vehicle.currentWarehouse.addVehicle(vehicle)

        return vehicle.currentWarehouse
    }

}
