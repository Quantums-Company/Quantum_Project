package org.bytebloom.domain.model

import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.validator.id.EntityIdValidator
import org.bytebloom.domain.validator.ValidatorError
import org.bytebloom.domain.validator.ValidatorField
import org.bytebloom.domain.validator.FieldValidator

class Route(
    val id: String,
    val distanceKm: Double,
    val typicalDelayMin: Int,
    val originWarehouse: Warehouse,
    val destinationWarehouse: Warehouse
) {
    init {
        val rules = FieldValidator()
        val violations = buildList {
            EntityIdValidator().validate(id, EntityType.ROUTE)?.let(::add)
            rules.positive(distanceKm, ValidatorField.DESTINATION_WAREHOUSE)?.let(::add)
            rules.nonNegative(typicalDelayMin, ValidatorField.TYPICAL_DELAY_MIN)?.let(::add)
            if (originWarehouse.id == destinationWarehouse.id) {
                add(ValidatorError.SameWarehouse(ValidatorField.ORIGIN_WAREHOUSE))
            }
        }
        if (violations.isNotEmpty()) throw EntityValidationException(violations)
    }
}