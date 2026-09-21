package org.bytebloom.domain.validator

class PackageIdValidator {

    operator fun invoke(id: String): ValidationResult {
        val violations = mutableListOf<FieldViolation>()

        if (id.isBlank()) {
            violations.add(FieldViolation.BlankField("id"))
        } else if (!id.startsWith("PKG-")) {
            violations.add(FieldViolation.InvalidPrefix("id", "PKG-"))
        }

        return if (violations.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(violations)
        }
    }
}