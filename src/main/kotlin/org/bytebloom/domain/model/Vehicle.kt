package org.bytebloom.domain.model

import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.validation.EntityType
import org.bytebloom.domain.validation.IdRules
import org.bytebloom.domain.validation.ValidationField
import org.bytebloom.domain.validation.ValidationRules

class Vehicle(
    val id: String,
    val maxCapacityKg: Double,
    val costPerKm: Double,
    val currentWarehouse: Warehouse   // كانت var — لازم val، شرح تحت
) {
    init {
        val rules = ValidationRules()
        val violations = listOfNotNull(
            IdRules.validate(id, EntityType.VEHICLE),
            rules.positive(maxCapacityKg, ValidationField.MaxCapacityKg()),
            rules.positive(costPerKm, ValidationField.CostPerKm())
        )
        if (violations.isNotEmpty()) throw EntityValidationException(violations)
    }

    fun canCarryWeight(weight: Double): Boolean = weight.isFinite() && weight > 0.0 && weight <= maxCapacityKg

    fun reassignedTo(newWarehouse: Warehouse): Vehicle =
        Vehicle(id, maxCapacityKg, costPerKm, newWarehouse)
}