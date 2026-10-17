package org.bytebloom.logiroute.domain.model

import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.validator.id.EntityIdValidator
import org.bytebloom.logiroute.domain.model.validation.ValidatorError
import org.bytebloom.logiroute.domain.model.validation.ValidatorField
import org.bytebloom.logiroute.domain.validator.FieldValidator

data class Package(
    val id: String,
    val weight: Double,
    val priority: Priority,
    val originWarehouse: Warehouse,
    val destinationWarehouse: Warehouse,
) {
    init {
        val rules = FieldValidator()
        val violations = buildList {
            EntityIdValidator().validate(id, EntityType.PACKAGE)?.let(::add)
            rules.positive(weight, ValidatorField.WEIGHT)?.let(::add)
            if (originWarehouse.id == destinationWarehouse.id) {
                add(ValidatorError.SameWarehouse(ValidatorField.ORIGIN_WAREHOUSE))
            }
        }
        if (violations.isNotEmpty()) throw EntityValidationException(violations)
    }

    fun redirectedTo(newDestination: Warehouse): Package =
        Package(id, weight, priority, originWarehouse, newDestination)
}