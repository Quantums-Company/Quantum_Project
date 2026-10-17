package org.bytebloom.logiroute.domain.usecase.vehicles

import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.repository.VehicleRepository
import org.bytebloom.logiroute.domain.model.validation.ValidatorResult
import org.bytebloom.logiroute.domain.validator.id.VehicleIdValidator

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