package org.bytebloom.domain.validator

import org.bytebloom.domain.model.Package

class CreatePackageValidator {

    operator fun invoke(pkg: Package): ValidationResult {
        val violations = mutableListOf<String>()

        if (pkg.id.isBlank()) {
            violations.add("Package ID cannot be blank")
        } else if (!pkg.id.startsWith("PKG-")) {
            violations.add("Package ID must start with PKG-")
        }

        if (pkg.weight <= 0) {
            violations.add("Package weight must be greater than 0")
        }

        if (pkg.originWarehouse.id.isBlank()) {
            violations.add("Origin warehouse ID cannot be blank")
        }

        if (pkg.destinationWarehouse.id.isBlank()) {
            violations.add("Destination warehouse ID cannot be blank")
        }

        return if (violations.isEmpty()) {
            ValidationResult.Valid()
        } else {
            ValidationResult.Invalid(violations)
        }
    }
}