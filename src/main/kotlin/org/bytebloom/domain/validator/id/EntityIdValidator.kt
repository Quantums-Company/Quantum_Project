package org.bytebloom.domain.validator.id

import org.bytebloom.domain.validation.EntityType
import org.bytebloom.domain.validation.IdRules
import org.bytebloom.domain.validation.ValidationResult
import org.bytebloom.domain.validation.toValidationResult

class EntityIdValidator {
    operator fun invoke(id: String, entityType: EntityType): ValidationResult =
        listOfNotNull(IdRules.validate(id, entityType)).toValidationResult()
}