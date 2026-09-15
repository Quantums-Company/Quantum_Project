package org.bytebloom.domain.usecase.crud.vehicle

import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.repository.VehicleRepository

class UpdateVehicleUseCase(
    private val vehicleRepository: VehicleRepository
) {
    suspend operator fun invoke(vehicle: Vehicle): Vehicle {
        return vehicleRepository.update(vehicle)
    }
}