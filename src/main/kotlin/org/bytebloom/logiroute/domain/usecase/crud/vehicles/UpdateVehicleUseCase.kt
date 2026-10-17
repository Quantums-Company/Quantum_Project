package org.bytebloom.logiroute.domain.usecase.crud.vehicles

import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.model.exception.ResourceNotFoundException
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.repository.VehicleRepository
import org.bytebloom.logiroute.domain.validator.update.UpdateVehicleValidator
import org.bytebloom.logiroute.domain.model.validation.ValidatorResult
import org.bytebloom.logiroute.domain.model.input.update.VehicleUpdateInput

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