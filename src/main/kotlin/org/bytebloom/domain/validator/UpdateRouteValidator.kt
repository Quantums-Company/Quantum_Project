package org.bytebloom.domain.validator

class UpdateRouteValidator {

    operator fun invoke(input: RouteUpdateInput): ValidationResult {
        val violations = mutableListOf<String>()

        if (input.id.isBlank()) {
            violations.add("Route ID cannot be blank")
        } else if (!input.id.startsWith("RT-")) {
            violations.add("Route ID must start with RT-")
        }

        val hasAnyUpdate =
            input.distanceKm != null ||
                    input.typicalDelayMin != null ||
                    input.originWarehouse != null ||
                    input.destinationWarehouse != null

        if (!hasAnyUpdate) {
            violations.add("At least one field must be provided for update")
        }

        if (input.distanceKm != null && input.distanceKm <= 0) {
            violations.add("Distance must be greater than 0")
        }

        if (input.typicalDelayMin != null && input.typicalDelayMin < 0) {
            violations.add("Typical delay cannot be negative")
        }

        if (input.originWarehouse != null && input.originWarehouse.id.isBlank()) {
            violations.add("Origin warehouse ID cannot be blank")
        }

        if (input.destinationWarehouse != null && input.destinationWarehouse.id.isBlank()) {
            violations.add("Destination warehouse ID cannot be blank")
        }

        return if (violations.isEmpty()) {
            ValidationResult.Valid()
        } else {
            ValidationResult.Invalid(violations)
        }
    }
}