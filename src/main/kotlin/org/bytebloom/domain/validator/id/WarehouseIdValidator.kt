package org.bytebloom.domain.validator.id

import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.validator.ValidatorResult

class WarehouseIdValidator {
    private val entityIdValidator = EntityIdValidator()
    operator fun invoke(id: String): ValidatorResult = entityIdValidator(id, EntityType.WAREHOUSE)
}