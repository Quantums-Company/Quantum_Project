package org.bytebloom.domain.usecase.crud.vehicle

import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.exception.ResourceNotFoundException
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.validator.update.UpdateVehicleValidator
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.model.updateInput.VehicleUpdateInput

class UpdateVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val validator: UpdateVehicleValidator
) {
    suspend operator fun invoke(input: VehicleUpdateInput): Vehicle {
        when (val result = validator(input)) {
            is ValidatorResult.Valid -> {
                val existing = vehicleRepository.getById(input.id)
                    ?: throw ResourceNotFoundException("Vehicle '${input.id}' was not found")

                val updated = Vehicle(
                    id = existing.id,
                    maxCapacityKg = input.maxCapacityKg ?: existing.maxCapacityKg,
                    costPerKm = input.costPerKm ?: existing.costPerKm,
                    currentWarehouse = input.currentWarehouse ?: existing.currentWarehouse
                )

                return vehicleRepository.update(updated)
            }

            is ValidatorResult.Invalid -> {
                throw EntityValidationException(result.violations)
            }
        }
    }
}