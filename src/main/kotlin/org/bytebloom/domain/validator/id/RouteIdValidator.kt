package org.bytebloom.domain.validator.id

import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.model.validation.ValidatorResult

class RouteIdValidator {
    private val entityIdValidator = EntityIdValidator()
    operator fun invoke(id: String): ValidatorResult = entityIdValidator(id, EntityType.ROUTE)
}