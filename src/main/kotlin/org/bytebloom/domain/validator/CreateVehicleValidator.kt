package org.bytebloom.domain.validator

import org.bytebloom.domain.model.Vehicle

class CreateVehicleValidator {

    operator fun invoke(vehicle: Vehicle): ValidationResult {
        val violations = mutableListOf<FieldViolation>()

        if (vehicle.id.isBlank()) {
            violations.add(FieldViolation.BlankField("id"))
        } else if (!vehicle.id.startsWith("TRK-")) {
            violations.add(FieldViolation.InvalidPrefix("id", "TRK-"))
        }

        if (vehicle.maxCapacityKg <= 0) {
            violations.add(FieldViolation.NotPositive("maxCapacityKg"))
        }

        if (vehicle.costPerKm <= 0) {
            violations.add(FieldViolation.NotPositive("costPerKm"))
        }

        if (vehicle.currentWarehouse.id.isBlank()) {
            violations.add(FieldViolation.BlankField("currentWarehouse.id"))
        }

        return if (violations.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(violations)
        }
    }
}