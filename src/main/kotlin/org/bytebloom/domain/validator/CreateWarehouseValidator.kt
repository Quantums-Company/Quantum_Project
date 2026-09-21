package org.bytebloom.domain.validator

import org.bytebloom.domain.model.Warehouse

class CreateWarehouseValidator {

    operator fun invoke(warehouse: Warehouse): ValidationResult {
        val violations = mutableListOf<FieldViolation>()

        if (warehouse.id.isBlank()) {
            violations.add(FieldViolation.BlankField("id"))
        } else if (!warehouse.id.startsWith("WH-")) {
            violations.add(FieldViolation.InvalidPrefix("id", "WH-"))
        }

        if (warehouse.name.isBlank()) {
            violations.add(FieldViolation.BlankField("name"))
        }

        if (warehouse.regionalZone.isBlank()) {
            violations.add(FieldViolation.BlankField("regionalZone"))
        }

        if (warehouse.latitude !in -90.0..90.0) {
            violations.add(FieldViolation.OutOfRange("latitude", -90.0, 90.0))
        }

        if (warehouse.longitude !in -180.0..180.0) {
            violations.add(FieldViolation.OutOfRange("longitude", -180.0, 180.0))
        }

        return if (violations.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(violations)
        }
    }
}