package org.bytebloom.logiroute.domain.usecase.crud.vehicles

import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.repository.VehicleRepository

class GetAllVehiclesUseCase(
    private val vehicleRepository: VehicleRepository
) {
    suspend operator fun invoke(): List<Vehicle> =
        vehicleRepository.getAll()
}