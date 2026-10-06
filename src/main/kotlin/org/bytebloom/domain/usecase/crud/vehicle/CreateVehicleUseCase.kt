package org.bytebloom.domain.usecase.crud.vehicle

import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.service.IdGenerator
import org.bytebloom.domain.model.EntityType

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