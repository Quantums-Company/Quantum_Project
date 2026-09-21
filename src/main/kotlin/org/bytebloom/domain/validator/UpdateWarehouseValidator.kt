package org.bytebloom.domain.validator

class UpdateWarehouseValidator {

    operator fun invoke(input: WarehouseUpdateInput): ValidationResult {
        val violations = mutableListOf<FieldViolation>()

        if (input.id.isBlank()) {
            violations.add(FieldViolation.BlankField("id"))
        } else if (!input.id.startsWith("WH-")) {
            violations.add(FieldViolation.InvalidPrefix("id", "WH-"))
        }

        val hasAnyUpdate =
            input.name != null ||
                    input.regionalZone != null ||
                    input.longitude != null ||
                    input.latitude != null

        if (!hasAnyUpdate) {
            violations.add(FieldViolation.NoFieldsProvided("Warehouse"))
        }

        if (input.name != null && input.name.isBlank()) {
            violations.add(FieldViolation.BlankField("name"))
        }

        if (input.regionalZone != null && input.regionalZone.isBlank()) {
            violations.add(FieldViolation.BlankField("regionalZone"))
        }

        if (input.latitude != null && input.latitude !in -90.0..90.0) {
            violations.add(FieldViolation.OutOfRange("latitude", -90.0, 90.0))
        }

        if (input.longitude != null && input.longitude !in -180.0..180.0) {
            violations.add(FieldViolation.OutOfRange("longitude", -180.0, 180.0))
        }

        return if (violations.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(violations)
        }
    }
}