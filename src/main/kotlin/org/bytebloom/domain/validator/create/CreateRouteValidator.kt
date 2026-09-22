package org.bytebloom.domain.validator.create

import org.bytebloom.domain.model.Route
import org.bytebloom.domain.validation.EntityType
import org.bytebloom.domain.validation.IdRules
import org.bytebloom.domain.validation.ValidationField
import org.bytebloom.domain.validation.ValidationResult
import org.bytebloom.domain.validation.ValidationRules
import org.bytebloom.domain.validation.toValidationResult

class CreateRouteValidator {
    private val rules = ValidationRules()

    operator fun invoke(route: Route): ValidationResult {
        val violations = listOfNotNull(
            IdRules.validate(route.id, EntityType.ROUTE),
            rules.positive(route.distanceKm, ValidationField.DistanceKm()),
            rules.nonNegative(route.typicalDelayMin, ValidationField.TypicalDelayMin()),
            rules.requiredText(route.originWarehouse.id, ValidationField.OriginWarehouse()),
            rules.requiredText(route.destinationWarehouse.id, ValidationField.DestinationWarehouse())
        )
        return violations.toValidationResult()
    }
}