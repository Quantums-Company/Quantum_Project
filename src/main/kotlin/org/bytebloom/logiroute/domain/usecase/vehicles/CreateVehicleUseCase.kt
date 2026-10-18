package org.bytebloom.logiroute.domain.usecase.vehicles

import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.repository.VehicleRepository
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.service.IdGenerator
import org.bytebloom.logiroute.domain.model.EntityType

class CreateVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val idGenerator: IdGenerator
) {
    suspend operator fun invoke(
        maxCapacityKg: Double,
        costPerKm: Double,
        currentWarehouse: Warehouse
    ): Vehicle {

        val vehicle = Vehicle(
            id = idGenerator.next(EntityType.VEHICLE),
            maxCapacityKg = maxCapacityKg,
            costPerKm = costPerKm,
            currentWarehouse = currentWarehouse )

        return vehicleRepository.create(vehicle)
    }
}