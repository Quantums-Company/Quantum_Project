package org.bytebloom.domain.validator

class UpdateRouteValidator {

    operator fun invoke(input: RouteUpdateInput): ValidationResult {
        val violations = buildList {
            validateId(input.id)
            validateHasUpdates(input)
            validateDistance(input.distanceKm)
            validateDelay(input.typicalDelayMin)
            validateWarehouse(input.originWarehouse?.id, "originWarehouse.id")
            validateWarehouse(input.destinationWarehouse?.id, "destinationWarehouse.id")
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
            !id.startsWith("RT-") -> add(FieldViolation.InvalidPrefix("id", "RT-"))
        }
    }

    private fun MutableList<FieldViolation>.validateHasUpdates(input: RouteUpdateInput) {
        val hasAnyUpdate = listOfNotNull(
            input.distanceKm,
            input.typicalDelayMin,
            input.originWarehouse,
            input.destinationWarehouse
        ).isNotEmpty()

        if (!hasAnyUpdate) {
            add(FieldViolation.NoFieldsProvided("Route"))
        }
    }

    private fun MutableList<FieldViolation>.validateDistance(distanceKm: Double?) {
        if (distanceKm != null && distanceKm <= 0) {
            add(FieldViolation.NotPositive("distanceKm"))
        }
    }

    private fun MutableList<FieldViolation>.validateDelay(typicalDelayMin: Int?) {
        if (typicalDelayMin != null && typicalDelayMin < 0) {
            add(FieldViolation.OutOfRange("typicalDelayMin", 0.0, Double.MAX_VALUE))
        }
    }

    private fun MutableList<FieldViolation>.validateWarehouse(warehouseId: String?, fieldName: String) {
        if (warehouseId != null && warehouseId.isBlank()) {
            add(FieldViolation.BlankField(fieldName))
        }
    }
}