package org.bytebloom.domain.model

import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.validator.id.EntityIdValidator
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField
import org.bytebloom.domain.validator.FieldValidator

class Package(
    val id: String,
    val weight: Double,
    val priority: Priority,
    val originWarehouse: Warehouse,
    val destinationWarehouse: Warehouse,
    val volumeM3: Double = 0.0
) {
    init {
        val rules = FieldValidator()
        val violations = buildList {
            EntityIdValidator().validate(id, EntityType.PACKAGE)?.let(::add)
            rules.positive(weight, ValidatorField.WEIGHT)?.let(::add)
            rules.nonNegative(volumeM3, ValidatorField.VOLUME_M3)?.let(::add)
            if (originWarehouse.id == destinationWarehouse.id) {
                add(ValidatorError.SameWarehouse(ValidatorField.ORIGIN_WAREHOUSE))
            }
        }
        if (violations.isNotEmpty()) throw EntityValidationException(violations)
    }

    fun redirectedTo(newDestination: Warehouse): Package =
        Package(id, weight, priority, originWarehouse, newDestination, volumeM3)

    override fun equals(other: Any?): Boolean = other is Package && id == other.id

    override fun hashCode(): Int = id.hashCode()
}