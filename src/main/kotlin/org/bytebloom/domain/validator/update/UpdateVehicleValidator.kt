package org.bytebloom.domain.validator.update

import org.bytebloom.domain.validation.EntityType
import org.bytebloom.domain.validation.IdRules
import org.bytebloom.domain.validation.ValidationError
import org.bytebloom.domain.validation.ValidationField
import org.bytebloom.domain.validation.ValidationResult
import org.bytebloom.domain.validation.ValidationRules
import org.bytebloom.domain.validation.input.VehicleUpdateInput
import org.bytebloom.domain.validation.toValidationResult

class UpdateVehicleValidator {
    private val rules = ValidationRules()

    operator fun invoke(input: VehicleUpdateInput): ValidationResult {
        val violations = buildList {
            IdRules.validate(input.id, EntityType.VEHICLE)?.let(::add)

            if (!input.hasUpdates()) {
                add(ValidationError.NoFieldsProvided(ValidationField.Entity()))
            }

            input.maxCapacityKg?.let { rules.positive(it, ValidationField.MaxCapacityKg())?.let(::add) }
            input.costPerKm?.let { rules.positive(it, ValidationField.CostPerKm())?.let(::add) }
            input.currentWarehouse?.let { rules.requiredText(it.id, ValidationField.CurrentWarehouse())?.let(::add) }
        }
        return violations.toValidationResult()
    }
}