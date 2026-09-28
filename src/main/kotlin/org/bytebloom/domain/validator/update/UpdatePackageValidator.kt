package org.bytebloom.domain.validator.update

import org.bytebloom.domain.validation.EntityType
import org.bytebloom.domain.validation.IdRules
import org.bytebloom.domain.validation.ValidationError
import org.bytebloom.domain.validation.ValidationField
import org.bytebloom.domain.validation.ValidationResult
import org.bytebloom.domain.validation.ValidationRules
import org.bytebloom.domain.validation.input.PackageUpdateInput
import org.bytebloom.domain.validation.toValidationResult

class UpdatePackageValidator {
    private val rules = ValidationRules()

    operator fun invoke(input: PackageUpdateInput): ValidationResult {
        val violations = buildList {
            IdRules.validate(input.id, EntityType.PACKAGE)?.let(::add)

            if (!input.hasUpdates()) {
                add(ValidationError.NoFieldsProvided(ValidationField.Entity()))
            }

            input.weight?.let { rules.positive(it, ValidationField.Weight())?.let(::add) }
            input.originWarehouse?.let { rules.requiredText(it.id, ValidationField.OriginWarehouse())?.let(::add) }
            input.destinationWarehouse?.let { rules.requiredText(it.id, ValidationField.DestinationWarehouse())?.let(::add) }
        }
        return violations.toValidationResult()
    }
}