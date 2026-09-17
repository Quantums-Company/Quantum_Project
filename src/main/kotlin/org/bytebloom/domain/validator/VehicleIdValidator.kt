package org.bytebloom.domain.validator

class VehicleIdValidator {

    operator fun invoke(id: String): ValidationResult {
        val violations = mutableListOf<String>()

        if (id.isBlank()) {
            violations.add("Vehicle ID cannot be blank")
        } else if (!id.startsWith("TRK-")) {
            violations.add("Vehicle ID must start with TRK-")
        }

        return if (violations.isEmpty()) {
            ValidationResult.Valid()
        } else {
            ValidationResult.Invalid(violations)
        }
    }
}