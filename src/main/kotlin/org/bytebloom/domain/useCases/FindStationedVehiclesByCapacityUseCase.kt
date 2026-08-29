package org.bytebloom.domain.useCases

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.Vehicle


class FindStationedVehiclesByCapacityUseCase() {

        operator fun invoke(warehouse: Warehouse, requiredCapacity: Double): List<Vehicle> =
            warehouse.stationedVehicles
                .filter { it.maxCapacityKg >= requiredCapacity }
                .sortedByDescending { it.maxCapacityKg }
    }
