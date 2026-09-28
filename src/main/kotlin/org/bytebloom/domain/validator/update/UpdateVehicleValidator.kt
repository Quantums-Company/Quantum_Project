package org.bytebloom.domain.validator.update

import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.validator.id.EntityIdValidator
import org.bytebloom.domain.validator.ValidatorError
import org.bytebloom.domain.validator.ValidatorField
import org.bytebloom.domain.validator.ValidatorResult
import org.bytebloom.domain.validator.FieldValidator
import org.bytebloom.domain.validator.input.VehicleUpdateInput
import org.bytebloom.domain.validator.toValidatorResult

class UpdateVehicleValidator {
    private val rules = FieldValidator()

    operator fun invoke(input: VehicleUpdateInput): ValidatorResult {
        val violations = buildList {
            EntityIdValidator().validate(input.id, EntityType.VEHICLE)?.let(::add)

            if (!input.hasUpdates()) {
                add(ValidatorError.NoFieldsProvided(ValidatorField.Entity()))
            }

            input.maxCapacityKg?.let { rules.positive(it, ValidatorField.MaxCapacityKg())?.let(::add) }
            input.costPerKm?.let { rules.positive(it, ValidatorField.CostPerKm())?.let(::add) }
            input.currentWarehouse?.let { rules.requiredText(it.id, ValidatorField.CurrentWarehouse())?.let(::add) }
        }
        return violations.toValidatorResult()
    }
}