package org.bytebloom.domain.validator

class RouteIdValidator {

    operator fun invoke(id: String): ValidationResult {
        val violations = mutableListOf<FieldViolation>()

        if (id.isBlank()) {
            violations.add(FieldViolation.BlankField("id"))
        } else if (!id.startsWith("RT-")) {
            violations.add(FieldViolation.InvalidPrefix("id", "RT-"))
        }

        return if (violations.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(violations)
        }
    }
}