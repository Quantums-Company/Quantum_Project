package org.bytebloom.domain.usecase.crud.vehicle

import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.validator.UpdateVehicleValidator
import org.bytebloom.domain.validator.ValidationResult
import org.bytebloom.domain.validator.VehicleUpdateInput

class UpdateVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val validator: UpdateVehicleValidator
) {
    suspend operator fun invoke(input: VehicleUpdateInput): Vehicle {
        when (val result = validator(input)) {
            is ValidationResult.Valid -> {
                val existing = vehicleRepository.getById(input.id)
                    ?: throw IllegalArgumentException("Vehicle '${input.id}' was not found")

                val updated = Vehicle(
                    id = existing.id,
                    maxCapacityKg = input.maxCapacityKg ?: existing.maxCapacityKg,
                    costPerKm = input.costPerKm ?: existing.costPerKm,
                    currentWarehouse = input.currentWarehouse ?: existing.currentWarehouse
                )

                return vehicleRepository.update(updated)
            }

            is ValidationResult.Invalid -> {
                throw IllegalArgumentException(
                    result.violations.joinToString(", ")
                )
            }
        }
    }
}