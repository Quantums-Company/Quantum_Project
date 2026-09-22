package org.bytebloom.domain.validator.create

import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.validation.EntityType
import org.bytebloom.domain.validation.IdRules
import org.bytebloom.domain.validation.ValidationField
import org.bytebloom.domain.validation.ValidationResult
import org.bytebloom.domain.validation.ValidationRules
import org.bytebloom.domain.validation.toValidationResult

class CreateVehicleValidator {
    private val rules = ValidationRules()

    operator fun invoke(vehicle: Vehicle): ValidationResult {
        val violations = listOfNotNull(
            IdRules.validate(vehicle.id, EntityType.VEHICLE),
            rules.positive(vehicle.maxCapacityKg, ValidationField.MaxCapacityKg()),
            rules.positive(vehicle.costPerKm, ValidationField.CostPerKm()),
            rules.requiredText(vehicle.currentWarehouse.id, ValidationField.CurrentWarehouse())
        )
        return violations.toValidationResult()
    }
}