package org.bytebloom.domain.validator

class UpdateWarehouseValidator {

    operator fun invoke(input : WarehouseUpdateInput): ValidationResult {
        val violations = mutableListOf<String>()

        if (input .id.isBlank()) {
            violations.add("Warehouse ID cannot be blank")
        } else if (!input.id.startsWith("WH-")) {
            violations.add("Warehouse ID must start with WH-")
        }

        val hasAnyUpdate =
            input.name != null ||
                    input.regionalZone != null ||
                    input.longitude != null ||
                    input.latitude != null

        if (!hasAnyUpdate) {
            violations.add("At least one field must be provided for update")
        }

        if (input.name != null && input.name.isBlank()) {
            violations.add("Warehouse name cannot be blank")
        }
        if (input.regionalZone != null && input.regionalZone.isBlank()) {
            violations.add("Regional zone cannot be blank")
        }
        if (input.latitude != null && input.latitude !in -90.0..90.0) {
            violations.add("Latitude must be between -90 and 90")
        }
        if (input.longitude != null && input.longitude !in -180.0..180.0) {
            violations.add("Longitude must be between -180 and 180")
        }
        return if (violations.isEmpty()) {
            ValidationResult.Valid()
        } else {
            ValidationResult.Invalid(violations)
        }
    }
}