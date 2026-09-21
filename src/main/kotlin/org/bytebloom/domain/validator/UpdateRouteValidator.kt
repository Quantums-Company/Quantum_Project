package org.bytebloom.domain.validator

class UpdateRouteValidator {

    operator fun invoke(input: RouteUpdateInput): ValidationResult {
        val violations = mutableListOf<FieldViolation>()

        if (input.id.isBlank()) {
            violations.add(FieldViolation.BlankField("id"))
        } else if (!input.id.startsWith("RT-")) {
            violations.add(FieldViolation.InvalidPrefix("id", "RT-"))
        }

        val hasAnyUpdate =
            input.distanceKm != null ||
                    input.typicalDelayMin != null ||
                    input.originWarehouse != null ||
                    input.destinationWarehouse != null

        if (!hasAnyUpdate) {
            violations.add(FieldViolation.NoFieldsProvided("Route"))
        }

        if (input.distanceKm != null && input.distanceKm <= 0) {
            violations.add(FieldViolation.NotPositive("distanceKm"))
        }

        if (input.typicalDelayMin != null && input.typicalDelayMin < 0) {
            violations.add(FieldViolation.OutOfRange("typicalDelayMin", 0.0, Double.MAX_VALUE))
        }

        if (input.originWarehouse != null && input.originWarehouse.id.isBlank()) {
            violations.add(FieldViolation.BlankField("originWarehouse.id"))
        }

        if (input.destinationWarehouse != null && input.destinationWarehouse.id.isBlank()) {
            violations.add(FieldViolation.BlankField("destinationWarehouse.id"))
        }

        return if (violations.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(violations)
        }
    }
}