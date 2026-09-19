package org.bytebloom.domain.validator

import org.bytebloom.domain.model.Warehouse

class CreateWarehouseValidator {

    operator fun invoke(warehouse: Warehouse): ValidationResult {
        val violations = mutableListOf<String>()

        if (warehouse.id.isBlank()) {
            violations.add("Warehouse ID cannot be blank")
        } else if (!warehouse.id.startsWith("WH-")) {
            violations.add("Warehouse ID must start with WH-")
        }

        if (warehouse.name.isBlank()) {
            violations.add("Warehouse name cannot be blank")
        }

        if (warehouse.regionalZone.isBlank()) {
            violations.add("Regional zone cannot be blank")
        }

        if (warehouse.latitude !in -90.0..90.0) {
            violations.add("Latitude must be between -90 and 90")
        }

        if (warehouse.longitude !in -180.0..180.0) {
            violations.add("Longitude must be between -180 and 180")
        }

        return if (violations.isEmpty()) {
            ValidationResult.Valid()
        } else {
            ValidationResult.Invalid(violations)
        }
    }
}