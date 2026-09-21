package org.bytebloom.domain.validator

class UpdatePackageValidator {

    operator fun invoke(input: PackageUpdateInput): ValidationResult {
        val violations = mutableListOf<FieldViolation>()

        if (input.id.isBlank()) {
            violations.add(FieldViolation.BlankField("id"))
        } else if (!input.id.startsWith("PKG-")) {
            violations.add(FieldViolation.InvalidPrefix("id", "PKG-"))
        }

        val hasAnyUpdate =
            input.weight != null ||
                    input.priority != null ||
                    input.originWarehouse != null ||
                    input.destinationWarehouse != null

        if (!hasAnyUpdate) {
            violations.add(FieldViolation.NoFieldsProvided("Package"))
        }

        if (input.weight != null && input.weight <= 0) {
            violations.add(FieldViolation.NotPositive("weight"))
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