package org.bytebloom.domain.validator.create

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.validation.EntityType
import org.bytebloom.domain.validation.IdRules
import org.bytebloom.domain.validation.ValidationField
import org.bytebloom.domain.validation.ValidationResult
import org.bytebloom.domain.validation.ValidationRules
import org.bytebloom.domain.validation.toValidationResult

class CreatePackageValidator {
    private val rules = ValidationRules()

    operator fun invoke(pkg: Package): ValidationResult {
        val violations = listOfNotNull(
            IdRules.validate(pkg.id, EntityType.PACKAGE),
            rules.positive(pkg.weight, ValidationField.Weight()),
            rules.requiredText(pkg.originWarehouse.id, ValidationField.OriginWarehouse()),
            rules.requiredText(pkg.destinationWarehouse.id, ValidationField.DestinationWarehouse())
        )
        return violations.toValidationResult()
    }
}