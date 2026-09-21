package org.bytebloom.domain.validator

import org.bytebloom.domain.model.Route

class CreateRouteValidator {

    operator fun invoke(route: Route): ValidationResult {
        val violations = mutableListOf<FieldViolation>()

        if (route.id.isBlank()) {
            violations.add(FieldViolation.BlankField("id"))
        } else if (!route.id.startsWith("RT-")) {
            violations.add(FieldViolation.InvalidPrefix("id", "RT-"))
        }

        if (route.distanceKm <= 0) {
            violations.add(FieldViolation.NotPositive("distanceKm"))
        }

        if (route.typicalDelayMin < 0) {
            violations.add(FieldViolation.OutOfRange("typicalDelayMin", 0.0, Double.MAX_VALUE))
        }

        if (route.originWarehouse.id.isBlank()) {
            violations.add(FieldViolation.BlankField("originWarehouse.id"))
        }

        if (route.destinationWarehouse.id.isBlank()) {
            violations.add(FieldViolation.BlankField("destinationWarehouse.id"))
        }

        return if (violations.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(violations)
        }
    }
}