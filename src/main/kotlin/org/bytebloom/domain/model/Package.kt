package org.bytebloom.domain.model

import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.validation.EntityType
import org.bytebloom.domain.validation.IdRules
import org.bytebloom.domain.validation.ValidationError
import org.bytebloom.domain.validation.ValidationField
import org.bytebloom.domain.validation.ValidationRules

class Package(
    val id: String,
    val weight: Double,
    val priority: Priority,
    val originWarehouse: Warehouse,
    val destinationWarehouse: Warehouse
) {
    init {
        val rules = ValidationRules()
        val violations = buildList {
            IdRules.validate(id, EntityType.PACKAGE)?.let(::add)
            rules.positive(weight, ValidationField.Weight())?.let(::add)
            if (originWarehouse.id == destinationWarehouse.id) {
                add(ValidationError.SameWarehouse(ValidationField.OriginWarehouse()))
            }
        }
        if (violations.isNotEmpty()) throw EntityValidationException(violations)
    }

    fun redirectedTo(newDestination: Warehouse): Package =
        Package(id, weight, priority, originWarehouse, newDestination)
}