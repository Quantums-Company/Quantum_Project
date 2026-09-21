package org.bytebloom.domain.validator

import org.bytebloom.domain.model.Package

class CreatePackageValidator {

    operator fun invoke(pkg: Package): ValidationResult {
        val violations = mutableListOf<FieldViolation>()

        if (pkg.id.isBlank()) {
            violations.add(FieldViolation.BlankField("id"))
        } else if (!pkg.id.startsWith("PKG-")) {
            violations.add(FieldViolation.InvalidPrefix("id", "PKG-"))
        }

        if (pkg.weight <= 0) {
            violations.add(FieldViolation.NotPositive("weight"))
        }

        if (pkg.originWarehouse.id.isBlank()) {
            violations.add(FieldViolation.BlankField("originWarehouse.id"))
        }

        if (pkg.destinationWarehouse.id.isBlank()) {
            violations.add(FieldViolation.BlankField("destinationWarehouse.id"))
        }

        return if (violations.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(violations)
        }
    }
}