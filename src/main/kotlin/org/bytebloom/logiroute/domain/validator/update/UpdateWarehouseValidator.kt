package org.bytebloom.logiroute.domain.validator.update

import org.bytebloom.logiroute.domain.model.EntityType
import org.bytebloom.logiroute.domain.validator.id.EntityIdValidator
import org.bytebloom.logiroute.domain.model.validation.ValidatorError
import org.bytebloom.logiroute.domain.model.validation.ValidatorField
import org.bytebloom.logiroute.domain.model.validation.ValidatorResult
import org.bytebloom.logiroute.domain.validator.FieldValidator
import org.bytebloom.logiroute.domain.model.input.update.WarehouseUpdateInput
import org.bytebloom.logiroute.domain.model.validation.toValidatorResult

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