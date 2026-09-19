package org.bytebloom.domain.validator

import org.bytebloom.domain.model.Vehicle

class CreateVehicleValidator {

    operator fun invoke(vehicle: Vehicle): ValidationResult {
        val violations = mutableListOf<String>()

        if (vehicle.id.isBlank()) {
            violations.add("Vehicle ID cannot be blank")
        }

        if (!vehicle.id.startsWith("TRK-")) {
            violations.add("Vehicle ID must start with TRK-")
        }

        if (vehicle.maxCapacityKg <= 0) {
            violations.add("Maximum capacity must be greater than 0")
        }

        if (vehicle.costPerKm <= 0) {
            violations.add("Cost per kilometer must be greater than 0")
        }

        if (vehicle.currentWarehouse.id.isBlank()) {
            violations.add("Current warehouse ID cannot be blank")
        }

        return if (violations.isEmpty()) {
            ValidationResult.Valid()
        } else {
            ValidationResult.Invalid(violations)
        }
    }
}