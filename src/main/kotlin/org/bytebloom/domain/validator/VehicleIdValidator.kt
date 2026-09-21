package org.bytebloom.domain.validator

class VehicleIdValidator {

    operator fun invoke(id: String): ValidationResult {
        val violations = mutableListOf<FieldViolation>()

        if (id.isBlank()) {
            violations.add(FieldViolation.BlankField("id"))
        } else if (!id.startsWith("TRK-")) {
            violations.add(FieldViolation.InvalidPrefix("id", "TRK-"))
        }

        return if (violations.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(violations)
        }
    }
}