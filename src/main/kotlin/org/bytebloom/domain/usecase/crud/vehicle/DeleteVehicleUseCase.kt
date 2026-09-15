package org.bytebloom.domain.usecase.crud.vehicle

import org.bytebloom.domain.repository.VehicleRepository

class DeleteVehicleUseCase(
    private val vehicleRepository: VehicleRepository
) {
    suspend operator fun invoke(id: String): Boolean {
        return vehicleRepository.delete(id)
    }
}