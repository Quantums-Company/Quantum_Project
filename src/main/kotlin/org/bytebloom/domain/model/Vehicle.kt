package org.bytebloom.domain.model

import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.validator.id.EntityIdValidator
import org.bytebloom.domain.model.validation.ValidatorField
import org.bytebloom.domain.validator.FieldValidator

class Vehicle(
    val id: String,
    val maxCapacityKg: Double,
    val costPerKm: Double,
    val currentWarehouse: Warehouse,
    val maxVolumeM3: Double = 0.0
) {
    init {
        val rules = FieldValidator()
        val violations = listOfNotNull(
            EntityIdValidator().validate(id, EntityType.VEHICLE),
            rules.positive(maxCapacityKg, ValidatorField.MAX_CAPACITY_KG),
            rules.positive(costPerKm, ValidatorField.COST_PER_KM),
            rules.nonNegative(maxVolumeM3, ValidatorField.MAX_VOLUME_M3)
        )
        if (violations.isNotEmpty()) throw EntityValidationException(violations)
    }

    fun canCarryWeight(weight: Double): Boolean = weight.isFinite() && weight >= 0.0 && weight <= maxCapacityKg

    fun reassignedTo(newWarehouse: Warehouse): Vehicle =
        Vehicle(id, maxCapacityKg, costPerKm, newWarehouse,maxVolumeM3)

    override fun equals(other: Any?): Boolean = other is Vehicle && id == other.id

    override fun hashCode(): Int = id.hashCode()
}