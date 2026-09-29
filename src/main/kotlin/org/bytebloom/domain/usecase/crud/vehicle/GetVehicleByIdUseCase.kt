package org.bytebloom.domain.usecase.crud.vehicle

import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.validator.id.VehicleIdValidator

class GetVehicleByIdUseCase(
    private val vehicleRepository: VehicleRepository,
    private val validator: VehicleIdValidator
) {
    suspend operator fun invoke(id: String): Vehicle? =
        when (val result = validator(id)) {
            is ValidatorResult.Valid -> vehicleRepository.getById(id)
            is ValidatorResult.Invalid -> throw EntityValidationException(result.violations)
        }
}