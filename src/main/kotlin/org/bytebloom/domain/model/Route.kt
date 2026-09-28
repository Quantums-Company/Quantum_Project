package org.bytebloom.domain.model

import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.validation.EntityType
import org.bytebloom.domain.validation.IdRules
import org.bytebloom.domain.validation.ValidationError
import org.bytebloom.domain.validation.ValidationField
import org.bytebloom.domain.validation.ValidationRules

class Route(
    val id: String,
    val distanceKm: Double,
    val typicalDelayMin: Int,
    val originWarehouse: Warehouse,
    val destinationWarehouse: Warehouse
) {
    init {
        val rules = ValidationRules()
        val violations = buildList {
            IdRules.validate(id, EntityType.ROUTE)?.let(::add)
            rules.positive(distanceKm, ValidationField.DistanceKm())?.let(::add)
            rules.nonNegative(typicalDelayMin, ValidationField.TypicalDelayMin())?.let(::add)
            if (originWarehouse.id == destinationWarehouse.id) {
                add(ValidationError.SameWarehouse(ValidationField.OriginWarehouse()))
            }
        }
        if (violations.isNotEmpty()) throw EntityValidationException(violations)
    }
}