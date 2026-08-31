package org.bytebloom.domain.usecase.required

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.Vehicle

class FindStationedVehiclesByCapacityUseCase {

    operator fun invoke(
        warehouse: Warehouse,
        requiredCapacityKg: Double
    ): List<Vehicle> =
        warehouse.stationedVehicles
            .filter { it.maxCapacityKg >= requiredCapacityKg }
}