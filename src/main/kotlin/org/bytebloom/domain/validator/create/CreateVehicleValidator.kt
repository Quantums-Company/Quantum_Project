package org.bytebloom.domain.validator.create

import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.validator.id.EntityIdValidator
import org.bytebloom.domain.validator.ValidatorField
import org.bytebloom.domain.validator.ValidatorResult
import org.bytebloom.domain.validator.FieldValidator
import org.bytebloom.domain.validator.toValidatorResult

class CreateVehicleValidator {
    private val rules = FieldValidator()

    operator fun invoke(vehicle: Vehicle): ValidatorResult {
        val violations = listOfNotNull(
            EntityIdValidator().validate(vehicle.id, EntityType.VEHICLE),
            rules.positive(vehicle.maxCapacityKg, ValidatorField.MaxCapacityKg()),
            rules.positive(vehicle.costPerKm, ValidatorField.CostPerKm()),
            rules.requiredText(vehicle.currentWarehouse.id, ValidatorField.CurrentWarehouse())
        )
        return violations.toValidatorResult()
    }
}