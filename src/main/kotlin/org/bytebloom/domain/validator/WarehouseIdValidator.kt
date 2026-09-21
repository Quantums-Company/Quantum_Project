package org.bytebloom.domain.validator

class WarehouseIdValidator {

    operator fun invoke(id: String): ValidationResult {
        val violations = mutableListOf<FieldViolation>()

        if (id.isBlank()) {
            violations.add(FieldViolation.BlankField("id"))
        } else if (!id.startsWith("WH-")) {
            violations.add(FieldViolation.InvalidPrefix("id", "WH-"))
        }

        return if (violations.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(violations)
        }
    }
}