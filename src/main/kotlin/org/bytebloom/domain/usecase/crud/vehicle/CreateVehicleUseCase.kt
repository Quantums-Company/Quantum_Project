package org.bytebloom.domain.usecase.crud.vehicle

import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.validator.create.CreateVehicleValidator
import org.bytebloom.domain.validation.ValidationResult

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