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
            rules.positive(route.distanceKm, ValidatorField.DistanceKm()),
            rules.nonNegative(route.typicalDelayMin, ValidatorField.TypicalDelayMin()),
            rules.requiredText(route.originWarehouse.id, ValidatorField.OriginWarehouse()),
            rules.requiredText(route.destinationWarehouse.id, ValidatorField.DestinationWarehouse())
        )
        return violations.toValidatorResult()
    }
}