package org.bytebloom.domain.usecase.crud.vehicle

import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.validator.CreateVehicleValidator
import org.bytebloom.domain.validator.ValidationResult

class CreateVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val validator: CreateVehicleValidator
) {
    suspend operator fun invoke(vehicle: Vehicle): Vehicle {
        when (val result = validator(vehicle)) {
            is ValidationResult.Valid -> {
                return vehicleRepository.create(vehicle)
            }

            is ValidationResult.Invalid -> {
                throw EntityValidationException(result.violations)
            }
        }
    }
}