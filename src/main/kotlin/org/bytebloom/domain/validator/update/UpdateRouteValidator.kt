package org.bytebloom.domain.validator.update

import org.bytebloom.domain.validation.EntityType
import org.bytebloom.domain.validation.IdRules
import org.bytebloom.domain.validation.ValidationError
import org.bytebloom.domain.validation.ValidationField
import org.bytebloom.domain.validation.ValidationResult
import org.bytebloom.domain.validation.ValidationRules
import org.bytebloom.domain.validation.input.RouteUpdateInput
import org.bytebloom.domain.validation.toValidationResult

class UpdateRouteValidator {
    private val rules = ValidationRules()

    operator fun invoke(input: RouteUpdateInput): ValidationResult {
        val violations = buildList {
            IdRules.validate(input.id, EntityType.ROUTE)?.let(::add)

            if (!input.hasUpdates()) {
                add(ValidationError.NoFieldsProvided(ValidationField.Entity()))
            }

            input.distanceKm?.let { rules.positive(it, ValidationField.DistanceKm())?.let(::add) }
            input.typicalDelayMin?.let { rules.nonNegative(it, ValidationField.TypicalDelayMin())?.let(::add) }
            input.originWarehouse?.let { rules.requiredText(it.id, ValidationField.OriginWarehouse())?.let(::add) }
            input.destinationWarehouse?.let { rules.requiredText(it.id, ValidationField.DestinationWarehouse())?.let(::add) }
        }
        return violations.toValidationResult()
    }
}