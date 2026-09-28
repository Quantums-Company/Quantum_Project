package org.bytebloom.domain.validator.create

import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.validator.id.EntityIdValidator
import org.bytebloom.domain.validator.ValidatorField
import org.bytebloom.domain.validator.ValidatorResult
import org.bytebloom.domain.validator.FieldValidator
import org.bytebloom.domain.validator.toValidatorResult

class CreateRouteValidator {
    private val rules = FieldValidator()

    operator fun invoke(route: Route): ValidatorResult {
        val violations = listOfNotNull(
            EntityIdValidator().validate(route.id, EntityType.ROUTE),
            rules.positive(route.distanceKm, ValidatorField.DISTANCE_KM),
            rules.nonNegative(route.typicalDelayMin, ValidatorField.TYPICAL_DELAY_MIN),
            rules.requiredText(route.originWarehouse.id, ValidatorField.ORIGIN_WAREHOUSE),
            rules.requiredText(route.destinationWarehouse.id, ValidatorField.DESTINATION_WAREHOUSE)
        )
        return violations.toValidatorResult()
    }
}