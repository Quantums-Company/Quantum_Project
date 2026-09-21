package org.bytebloom.domain.validator

class UpdateWarehouseValidator {

    companion object {
        private const val ENTITY_NAME = "Warehouse"
        private const val PREFIX_WAREHOUSE_ID = "WH-"
        private const val MIN_LATITUDE = -90.0
        private const val MAX_LATITUDE = 90.0
        private const val MIN_LONGITUDE = -180.0
        private const val MAX_LONGITUDE = 180.0
    }

    operator fun invoke(input: WarehouseUpdateInput): ValidationResult {
        val violations = buildList {
            validateId(input.id)
            validateAtLeastOneUpdate(input)
            validateName(input.name)
            validateRegionalZone(input.regionalZone)
            validateLatitude(input.latitude)
            validateLongitude(input.longitude)
        }

        return if (violations.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(violations)
        }
    }

    private fun MutableList<FieldViolation>.validateId(id: String) {
        when {
            id.isBlank() -> add(FieldViolation.BlankField("id"))
            !id.startsWith(PREFIX_WAREHOUSE_ID) -> add(
                FieldViolation.InvalidPrefix("id", PREFIX_WAREHOUSE_ID)
            )
        }
    }

    private fun MutableList<FieldViolation>.validateAtLeastOneUpdate(input: WarehouseUpdateInput) {
        val hasAnyUpdate = input.name != null ||
                input.regionalZone != null ||
                input.longitude != null ||
                input.latitude != null

        if (!hasAnyUpdate) {
            add(FieldViolation.NoFieldsProvided(ENTITY_NAME))
        }
    }

    private fun MutableList<FieldViolation>.validateName(name: String?) {
        if (name != null && name.isBlank()) {
            add(FieldViolation.BlankField("name"))
        }
    }

    private fun MutableList<FieldViolation>.validateRegionalZone(regionalZone: String?) {
        if (regionalZone != null && regionalZone.isBlank()) {
            add(FieldViolation.BlankField("regionalZone"))
        }
    }

    private fun MutableList<FieldViolation>.validateLatitude(latitude: Double?) {
        if (latitude != null && latitude !in MIN_LATITUDE..MAX_LATITUDE) {
            add(FieldViolation.OutOfRange("latitude", MIN_LATITUDE, MAX_LATITUDE))
        }
    }

    private fun MutableList<FieldViolation>.validateLongitude(longitude: Double?) {
        if (longitude != null && longitude !in MIN_LONGITUDE..MAX_LONGITUDE) {
            add(FieldViolation.OutOfRange("longitude", MIN_LONGITUDE, MAX_LONGITUDE))
        }
    }
}