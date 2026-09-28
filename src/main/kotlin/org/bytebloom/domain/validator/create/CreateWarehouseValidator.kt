package org.bytebloom.domain.validator.create

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.validator.id.EntityIdValidator
import org.bytebloom.domain.validator.ValidatorField
import org.bytebloom.domain.validator.ValidatorResult
import org.bytebloom.domain.validator.FieldValidator
import org.bytebloom.domain.validator.toValidatorResult

class CreateWarehouseValidator {
    private val rules = FieldValidator()

    operator fun invoke(warehouse: Warehouse): ValidatorResult {
        val violations = listOfNotNull(
            EntityIdValidator().validate(warehouse.id, EntityType.WAREHOUSE),
            rules.requiredText(warehouse.name, ValidatorField.Name()),
            rules.requiredText(warehouse.regionalZone, ValidatorField.RegionalZone()),
            rules.latitude(warehouse.latitude),
            rules.longitude(warehouse.longitude)
        )
        return violations.toValidatorResult()
    }
}