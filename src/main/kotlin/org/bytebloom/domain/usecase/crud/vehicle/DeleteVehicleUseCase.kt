package org.bytebloom.domain.usecase.crud.vehicle

import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.validation.ValidationResult
import org.bytebloom.domain.validator.id.VehicleIdValidator

class DeleteVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val validator: VehicleIdValidator
) {
    suspend operator fun invoke(id: String): Boolean =
        when (val result = validator(id)) {
            is ValidationResult.Valid -> vehicleRepository.delete(id)
            is ValidationResult.Invalid -> throw EntityValidationException(result.violations)
        }
}