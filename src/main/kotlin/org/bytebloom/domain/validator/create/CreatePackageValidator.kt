package org.bytebloom.domain.validator.create

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.validator.id.EntityIdValidator
import org.bytebloom.domain.model.validation.ValidatorField
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.validator.FieldValidator
import org.bytebloom.domain.model.validation.toValidatorResult

class CreatePackageValidator {
    private val rules = FieldValidator()

    operator fun invoke(pkg: Package): ValidatorResult {
        val violations = listOfNotNull(
            EntityIdValidator().validate(pkg.id, EntityType.PACKAGE),
            rules.positive(pkg.weight, ValidatorField.WEIGHT),
            rules.requiredText(pkg.originWarehouse.id, ValidatorField.ORIGIN_WAREHOUSE),
            rules.requiredText(pkg.destinationWarehouse.id, ValidatorField.DESTINATION_WAREHOUSE)
        )
        return violations.toValidatorResult()
    }
}