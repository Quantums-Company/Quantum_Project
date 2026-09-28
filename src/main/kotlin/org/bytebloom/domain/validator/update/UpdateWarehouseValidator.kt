package org.bytebloom.domain.validator.update

import org.bytebloom.domain.validation.EntityType
import org.bytebloom.domain.validation.IdRules
import org.bytebloom.domain.validation.ValidationError
import org.bytebloom.domain.validation.ValidationField
import org.bytebloom.domain.validation.ValidationResult
import org.bytebloom.domain.validation.ValidationRules
import org.bytebloom.domain.validation.input.WarehouseUpdateInput
import org.bytebloom.domain.validation.toValidationResult

class UpdateWarehouseValidator {
    private val rules = ValidationRules()

    operator fun invoke(input: WarehouseUpdateInput): ValidationResult {
        val violations = buildList {
            IdRules.validate(input.id, EntityType.WAREHOUSE)?.let(::add)

            if (!input.hasUpdates()) {
                add(ValidationError.NoFieldsProvided(ValidationField.Entity()))
            }

            input.name?.let { rules.requiredText(it, ValidationField.Name())?.let(::add) }
            input.regionalZone?.let { rules.requiredText(it, ValidationField.RegionalZone())?.let(::add) }
            input.latitude?.let { rules.latitude(it)?.let(::add) }
            input.longitude?.let { rules.longitude(it)?.let(::add) }
        }
        return violations.toValidationResult()
    }
}