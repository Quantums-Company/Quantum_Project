package org.bytebloom.domain.validation

object IdRules {

    private const val SEQUENTIAL_ID_PATTERN = """\d{3,}"""
    private const val UUID_PATTERN =
        """[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"""

    fun validate(id: String, entityType: EntityType): ValidationError? {
        if (id.isBlank()) {
            return ValidationError.Blank(ValidationField.Id())
        }

        val pattern = Regex(
            "^${Regex.escape(entityType.idPrefix)}($SEQUENTIAL_ID_PATTERN|$UUID_PATTERN)$"
        )

        return if (!pattern.matches(id)) {
            ValidationError.InvalidIdFormat(field = ValidationField.Id(), entityType = entityType)
        } else {
            null
        }
    }
}