package org.bytebloom.domain.validator.update

import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.validator.id.EntityIdValidator
import org.bytebloom.domain.validator.ValidatorError
import org.bytebloom.domain.validator.ValidatorField
import org.bytebloom.domain.validator.ValidatorResult
import org.bytebloom.domain.validator.FieldValidator
import org.bytebloom.domain.validator.input.RouteUpdateInput
import org.bytebloom.domain.validator.toValidatorResult

class UpdateRouteValidator {
    private val rules = FieldValidator()

    operator fun invoke(input: RouteUpdateInput): ValidatorResult {
        val violations = buildList {
            EntityIdValidator().validate(input.id, EntityType.ROUTE)?.let(::add)

            if (!input.hasUpdates()) {
                add(ValidatorError.NoFieldsProvided(ValidatorField.Entity()))
            }

            input.distanceKm?.let { rules.positive(it, ValidatorField.DistanceKm())?.let(::add) }
            input.typicalDelayMin?.let { rules.nonNegative(it, ValidatorField.TypicalDelayMin())?.let(::add) }
            input.originWarehouse?.let { rules.requiredText(it.id, ValidatorField.OriginWarehouse())?.let(::add) }
            input.destinationWarehouse?.let { rules.requiredText(it.id, ValidatorField.DestinationWarehouse())?.let(::add) }
        }
        return violations.toValidatorResult()
    }
}