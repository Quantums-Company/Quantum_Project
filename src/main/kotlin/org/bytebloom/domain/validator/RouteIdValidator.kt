package org.bytebloom.domain.validator

class RouteIdValidator {

    operator fun invoke(id: String): ValidationResult {
        val violations = mutableListOf<String>()

        if (id.isBlank()) {
            violations.add("Route ID cannot be blank")
        } else if (!id.startsWith("RT-")) {
            violations.add("Route ID must start with RT-")
        }

        return if (violations.isEmpty()) {
            ValidationResult.Valid()
        } else {
            ValidationResult.Invalid(violations)
        }
    }
}