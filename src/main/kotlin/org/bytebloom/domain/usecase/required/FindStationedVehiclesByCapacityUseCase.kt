package org.bytebloom.domain.usecase.required

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.Vehicle


class FindStationedVehiclesByCapacityUseCase() {

    operator fun invoke(vehicle: List<Vehicle>, requiredCapacity: Double): List<Vehicle> {//warehouse: Warehouse,
        val vehicle = vehicle.filter { it.maxCapacityKg >= requiredCapacity }
        return vehicle
    }
}