package org.bytebloom.logiroute.domain.usecase.vehicles

import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.Vehicle

class FindStationedVehiclesByCapacityUseCase {

    operator fun invoke(
        warehouse: Warehouse,
        requiredCapacityKg: Double
    ): List<Vehicle> =
        warehouse.stationedVehicles
            .filter { it.maxCapacityKg >= requiredCapacityKg }
}