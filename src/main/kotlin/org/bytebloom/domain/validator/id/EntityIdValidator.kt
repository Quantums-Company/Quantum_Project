package org.bytebloom.domain.validator.id

import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.model.validation.toValidatorResult

class EntityIdValidator {

    operator fun invoke(id: String, entityType: EntityType): ValidatorResult =
        listOfNotNull(validate(id, entityType)).toValidatorResult()

    fun validate(id: String, entityType: EntityType): ValidatorError? {
        if (id.isBlank()) {
            return ValidatorError.Blank(ValidatorField.ID)
        }

        val pattern = Regex(
            "^${Regex.escape(entityType.idPrefix)}($SEQUENTIAL_ID_PATTERN|$UUID_PATTERN)$"
        )

        return if (pattern.matches(id)) {
            null
        } else {
            ValidatorError.InvalidIdFormat(field = ValidatorField.ID, entityType = entityType)
        }
    }

    private companion object {
        private const val SEQUENTIAL_ID_PATTERN = """\d{3,}"""
        private const val UUID_PATTERN =
            """[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"""
    }
}
