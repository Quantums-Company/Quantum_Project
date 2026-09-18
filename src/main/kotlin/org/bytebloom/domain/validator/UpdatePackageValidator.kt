package org.bytebloom.domain.validator

class UpdatePackageValidator {

    operator fun invoke(input: PackageUpdateInput): ValidationResult {
        val violations = mutableListOf<String>()

        if (input.id.isBlank()) {
            violations.add("Package ID cannot be blank")
        } else if (!input.id.startsWith("PKG-")) {
            violations.add("Package ID must start with PKG-")
        }

        val hasAnyUpdate =
            input.weight != null ||
                    input.priority != null ||
                    input.originWarehouse != null ||
                    input.destinationWarehouse != null

        if (!hasAnyUpdate) {
            violations.add("At least one field must be provided for update")
        }

        if (input.weight != null && input.weight <= 0) {
            violations.add("Package weight must be greater than 0")
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