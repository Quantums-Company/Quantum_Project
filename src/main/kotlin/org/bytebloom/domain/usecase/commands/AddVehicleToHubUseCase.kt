package org.bytebloom.domain.usecase.commands

import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse

class AddVehicleToHubUseCase {

    operator fun invoke(
        warehouse: Warehouse,
        vehicle: Vehicle
    ){
        warehouse.addVehicle(vehicle)
        vehicle.currentWarehouse = warehouse
        //ToDO("we should update the resource data")
    }

}
