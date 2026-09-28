package org.bytebloom.domain.validator.update

import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.validator.id.EntityIdValidator
import org.bytebloom.domain.validator.ValidatorError
import org.bytebloom.domain.validator.ValidatorField
import org.bytebloom.domain.validator.ValidatorResult
import org.bytebloom.domain.validator.FieldValidator
import org.bytebloom.domain.validator.input.PackageUpdateInput
import org.bytebloom.domain.validator.toValidatorResult

class UpdatePackageValidator {
    private val rules = FieldValidator()

    operator fun invoke(input: PackageUpdateInput): ValidatorResult {
        val violations = buildList {
            EntityIdValidator().validate(input.id, EntityType.PACKAGE)?.let(::add)

            if (!input.hasUpdates()) {
                add(ValidatorError.NoFieldsProvided(ValidatorField.Entity()))
            }

            input.weight?.let { rules.positive(it, ValidatorField.Weight())?.let(::add) }
            input.originWarehouse?.let { rules.requiredText(it.id, ValidatorField.OriginWarehouse())?.let(::add) }
            input.destinationWarehouse?.let { rules.requiredText(it.id, ValidatorField.DestinationWarehouse())?.let(::add) }
        }
        return violations.toValidatorResult()
    }
}