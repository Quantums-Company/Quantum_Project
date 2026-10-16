package org.bytebloom.domain.usecase.crud.vehicles

import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.repository.VehicleRepository

class GetAllVehiclesUseCase(
    private val vehicleRepository: VehicleRepository
) {
    suspend operator fun invoke(): List<Vehicle> =
        vehicleRepository.getAll()
}