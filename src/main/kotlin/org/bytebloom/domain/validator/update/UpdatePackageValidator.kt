package org.bytebloom.domain.validator.update

import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.validator.id.EntityIdValidator
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.validator.FieldValidator
import org.bytebloom.domain.model.input.update.PackageUpdateInput
import org.bytebloom.domain.model.validation.toValidatorResult

class UpdatePackageValidator {
    private val rules = FieldValidator()

    operator fun invoke(input: PackageUpdateInput): ValidatorResult {
        val violations = buildList {
            EntityIdValidator().validate(input.id, EntityType.PACKAGE)?.let(::add)

            if (hasSameWarehouse(input)) {
                add(ValidatorError.SameWarehouse(ValidatorField.ORIGIN_WAREHOUSE))
            }

            if (!input.hasUpdates()) {
                add(ValidatorError.NoFieldsProvided(ValidatorField.ENTITY))
            }

            input.weight?.let {
                rules.positive(it, ValidatorField.WEIGHT)?.let(::add)
            }
            input.originWarehouse?.let {
                rules.requiredText(it.id, ValidatorField.ORIGIN_WAREHOUSE)?.let(::add)
            }
            input.destinationWarehouse?.let {
                rules.requiredText(it.id, ValidatorField.DESTINATION_WAREHOUSE)?.let(::add)
            }
        }
        return violations.toValidatorResult()
    }
    private fun hasSameWarehouse(input: PackageUpdateInput): Boolean =
        input.originWarehouse != null &&
                input.destinationWarehouse != null &&
                input.originWarehouse.id == input.destinationWarehouse.id
}