package org.bytebloom.domain.validator

class UpdateVehicleValidator {

    operator fun invoke(input: VehicleUpdateInput): ValidationResult {
        val violations = mutableListOf<FieldViolation>()

        if (input.id.isBlank()) {
            violations.add(FieldViolation.BlankField("id"))
        } else if (!input.id.startsWith("TRK-")) {
            violations.add(FieldViolation.InvalidPrefix("id", "TRK-"))
        }

        val hasAnyUpdate =
            input.maxCapacityKg != null ||
                    input.costPerKm != null ||
                    input.currentWarehouse != null

        if (!hasAnyUpdate) {
            violations.add(FieldViolation.NoFieldsProvided("Vehicle"))
        }

        if (input.maxCapacityKg != null && input.maxCapacityKg <= 0) {
            violations.add(FieldViolation.NotPositive("maxCapacityKg"))
        }

        if (input.costPerKm != null && input.costPerKm <= 0) {
            violations.add(FieldViolation.NotPositive("costPerKm"))
        }

        if (input.currentWarehouse != null && input.currentWarehouse.id.isBlank()) {
            violations.add(FieldViolation.BlankField("currentWarehouse.id"))
        }

        return if (violations.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(violations)
        }
    }
}