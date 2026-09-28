package org.bytebloom.domain.validator.create

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.validation.EntityType
import org.bytebloom.domain.validation.IdRules
import org.bytebloom.domain.validation.ValidationField
import org.bytebloom.domain.validation.ValidationResult
import org.bytebloom.domain.validation.ValidationRules
import org.bytebloom.domain.validation.toValidationResult

class CreateWarehouseValidator {
    private val rules = ValidationRules()

    operator fun invoke(warehouse: Warehouse): ValidationResult {
        val violations = listOfNotNull(
            IdRules.validate(warehouse.id, EntityType.WAREHOUSE),
            rules.requiredText(warehouse.name, ValidationField.Name()),
            rules.requiredText(warehouse.regionalZone, ValidationField.RegionalZone()),
            rules.latitude(warehouse.latitude),
            rules.longitude(warehouse.longitude)
        )
        return violations.toValidationResult()
    }
}