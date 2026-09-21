package org.bytebloom.domain.validator

import org.bytebloom.domain.model.Warehouse

class CreateWarehouseValidator {

    operator fun invoke(warehouse: Warehouse): ValidationResult {
        val violations = buildList {
            when {
                warehouse.id.isBlank() -> add(FieldViolation.BlankField("id"))
                !warehouse.id.startsWith(PREFIX_WAREHOUSE_ID) -> add(
                    FieldViolation.InvalidPrefix("id", PREFIX_WAREHOUSE_ID)
                )
            }

            if (warehouse.name.isBlank()) {
                add(FieldViolation.BlankField("name"))
            }

            if (warehouse.regionalZone.isBlank()) {
                add(FieldViolation.BlankField("regionalZone"))
            }

            if (warehouse.latitude !in MIN_LATITUDE..MAX_LATITUDE) {
                add(FieldViolation.OutOfRange("latitude", MIN_LATITUDE, MAX_LATITUDE))
            }

            if (warehouse.longitude !in MIN_LONGITUDE..MAX_LONGITUDE) {
                add(FieldViolation.OutOfRange("longitude", MIN_LONGITUDE, MAX_LONGITUDE))
            }
        }

        return if (violations.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(violations)
    }

    companion object {
        private const val PREFIX_WAREHOUSE_ID = "WH-"
        private const val MIN_LATITUDE = -90.0
        private const val MAX_LATITUDE = 90.0
        private const val MIN_LONGITUDE = -180.0
        private const val MAX_LONGITUDE = 180.0
    }
}