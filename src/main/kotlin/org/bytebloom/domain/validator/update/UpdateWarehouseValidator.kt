package org.bytebloom.domain.validator.update

import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.validator.id.EntityIdValidator
import org.bytebloom.domain.validator.ValidatorError
import org.bytebloom.domain.validator.ValidatorField
import org.bytebloom.domain.validator.ValidatorResult
import org.bytebloom.domain.validator.FieldValidator
import org.bytebloom.domain.validator.input.WarehouseUpdateInput
import org.bytebloom.domain.validator.toValidatorResult

class UpdateWarehouseValidator {
    private val rules = FieldValidator()

    operator fun invoke(input: WarehouseUpdateInput): ValidatorResult {
        val violations = buildList {
            EntityIdValidator().validate(input.id, EntityType.WAREHOUSE)?.let(::add)

            if (!input.hasUpdates()) {
                add(ValidatorError.NoFieldsProvided(ValidatorField.ENTITY))
            }

            input.name?.let { rules.requiredText(it, ValidatorField.NAME)?.let(::add) }
            input.regionalZone?.let { rules.requiredText(it, ValidatorField.REGIONAL_ZONE)?.let(::add) }
            input.latitude?.let { rules.latitude(it)?.let(::add) }
            input.longitude?.let { rules.longitude(it)?.let(::add) }
        }
        return violations.toValidatorResult()
    }
}