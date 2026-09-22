package org.bytebloom.domain.validator.id

import org.bytebloom.domain.validation.EntityType
import org.bytebloom.domain.validation.ValidationResult

class VehicleIdValidator {
    private val entityIdValidator = EntityIdValidator()
    operator fun invoke(id: String): ValidationResult = entityIdValidator(id, EntityType.VEHICLE)
}