package org.bytebloom.domain.usecase.crud.vehicle

import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.validator.create.CreateVehicleValidator
import org.bytebloom.domain.validation.ValidationResult
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.service.IdGenerator
import org.bytebloom.domain.validation.EntityType


class CreateVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val validator: CreateVehicleValidator,
    private val idGenerator: IdGenerator
) {
    suspend operator fun invoke( maxCapacityKg: Double, costPerKm: Double, currentWarehouse: Warehouse): Vehicle {

        val vehicle = Vehicle(
            id = idGenerator.next(EntityType.VEHICLE.idPrefix),
            maxCapacityKg = maxCapacityKg,
            costPerKm = costPerKm,
            currentWarehouse = currentWarehouse )

        return when (val result = validator(vehicle)) {
            is ValidationResult.Valid -> { vehicleRepository.create(vehicle) }

            is ValidationResult.Invalid -> { throw EntityValidationException(result.violations) }
        }
    }
}