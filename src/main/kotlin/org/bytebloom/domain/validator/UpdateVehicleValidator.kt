package org.bytebloom.domain.validator

class UpdateVehicleValidator {

    operator fun invoke(input: VehicleUpdateInput): ValidationResult {
        val violations = mutableListOf<String>()

        if (input.id.isBlank()) {
            violations.add("Vehicle ID cannot be blank")
        } else if (!input.id.startsWith("TRK-")) {
            violations.add("Vehicle ID must start with TRK-")
        }

        val hasAnyUpdate =
            input.maxCapacityKg != null ||
                    input.costPerKm != null ||
                    input.currentWarehouse != null

        if (!hasAnyUpdate) {
            violations.add("At least one field must be provided for update")
        }

        if (input.maxCapacityKg != null && input.maxCapacityKg <= 0) {
            violations.add("Maximum capacity must be greater than 0")
        }

        if (input.costPerKm != null && input.costPerKm <= 0) {
            violations.add("Cost per kilometer must be greater than 0")
        }

        if (input.currentWarehouse != null && input.currentWarehouse.id.isBlank()) {
            violations.add("Current warehouse ID cannot be blank")
        }

        return if (violations.isEmpty()) {
            ValidationResult.Valid()
        } else {
            ValidationResult.Invalid(violations)
        }
    }
}