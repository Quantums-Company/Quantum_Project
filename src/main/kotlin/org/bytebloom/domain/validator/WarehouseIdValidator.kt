package org.bytebloom.domain.validator

class WarehouseIdValidator {

    operator fun invoke(id: String): ValidationResult {
        val violations = mutableListOf<String>()

        if (id.isBlank()) {
            violations.add("Warehouse ID cannot be blank")
        } else if (!id.startsWith("WH-")) {
            violations.add("Warehouse ID must start with WH-")
        }

        return if (violations.isEmpty()) {
            ValidationResult.Valid()
        } else {
            ValidationResult.Invalid(violations)
        }
    }
}