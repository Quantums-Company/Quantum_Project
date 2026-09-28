package org.bytebloom.domain.validator.create

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.validator.id.EntityIdValidator
import org.bytebloom.domain.validator.ValidatorField
import org.bytebloom.domain.validator.ValidatorResult
import org.bytebloom.domain.validator.FieldValidator
import org.bytebloom.domain.validator.toValidatorResult

class CreatePackageValidator {
    private val rules = FieldValidator()

    operator fun invoke(pkg: Package): ValidatorResult {
        val violations = listOfNotNull(
            EntityIdValidator().validate(pkg.id, EntityType.PACKAGE),
            rules.positive(pkg.weight, ValidatorField.Weight()),
            rules.requiredText(pkg.originWarehouse.id, ValidatorField.OriginWarehouse()),
            rules.requiredText(pkg.destinationWarehouse.id, ValidatorField.DestinationWarehouse())
        )
        return violations.toValidatorResult()
    }
}