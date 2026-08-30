package org.bytebloom.domain.usecase.required

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.Vehicle


class DispatchVehicleUseCase {
    operator fun invoke(warehouse: Warehouse, vehicle: Vehicle): List<Vehicle> =
        warehouse.stationedVehicles.filter { it.id != vehicle.id }
}
