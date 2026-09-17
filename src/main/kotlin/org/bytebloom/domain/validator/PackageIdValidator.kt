package org.bytebloom.domain.validator

class PackageIdValidator {

    operator fun invoke(id: String): ValidationResult {
        val violations = mutableListOf<String>()

        if (id.isBlank()) {
            violations.add("Package ID cannot be blank")
        } else if (!id.startsWith("PKG-")) {
            violations.add("Package ID must start with PKG-")
        }

        return if (violations.isEmpty()) {
            ValidationResult.Valid()
        } else {
            ValidationResult.Invalid(violations)
        }
    }
}