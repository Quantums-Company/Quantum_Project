package org.bytebloom.domain.validator

import org.bytebloom.domain.model.Route

class CreateRouteValidator {

    operator fun invoke(route: Route): ValidationResult {
        val violations = mutableListOf<String>()

        if (route.id.isBlank()) {
            violations.add("Route ID cannot be blank")
        }

        if (!route.id.startsWith("RT-")) {
            violations.add("Route ID must start with RT-")
        }

        if (route.distanceKm <= 0) {
            violations.add("Distance must be greater than 0")
        }

        if (route.typicalDelayMin < 0) {
            violations.add("Typical delay cannot be negative")
        }

        if (route.originWarehouse.id.isBlank()) {
            violations.add("Origin warehouse ID cannot be blank")
        }

        if (route.destinationWarehouse.id.isBlank()) {
            violations.add("Destination warehouse ID cannot be blank")
        }

        return if (violations.isEmpty()) {
            ValidationResult.Valid()
        } else {
            ValidationResult.Invalid(violations)
        }
    }
}