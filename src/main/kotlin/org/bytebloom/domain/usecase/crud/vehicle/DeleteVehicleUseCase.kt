package org.bytebloom.domain.usecase.crud.vehicle

import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.validator.ValidatorResult
import org.bytebloom.domain.validator.id.VehicleIdValidator

class DeleteVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val validator: VehicleIdValidator
) {
    suspend operator fun invoke(id: String): Boolean =
        when (val result = validator(id)) {
            is ValidatorResult.Valid -> vehicleRepository.delete(id)
            is ValidatorResult.Invalid -> throw EntityValidationException(result.violations)
        }
}