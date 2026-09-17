package org.bytebloom.domain.usecase.crud.vehicle

import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.validator.UpdateVehicleValidator
import org.bytebloom.domain.validator.ValidationResult

class UpdateVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val validator: UpdateVehicleValidator
) {
    suspend operator fun invoke(vehicle: Vehicle): Vehicle {

        when (val result = validator(vehicle)) {
            is ValidationResult.Valid -> {
                return vehicleRepository.update(vehicle)
            }

            is ValidationResult.Invalid -> {
                throw IllegalArgumentException(
                    result.violations.joinToString(", ")
                )
            }
        }
    }
}