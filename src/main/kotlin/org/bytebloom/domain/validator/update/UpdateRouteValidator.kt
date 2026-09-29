package org.bytebloom.domain.validator.update

import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.validator.id.EntityIdValidator
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.validator.FieldValidator
import org.bytebloom.domain.model.updateInput.RouteUpdateInput
import org.bytebloom.domain.model.validation.toValidatorResult

class UpdateRouteValidator {
    private val rules = FieldValidator()

    operator fun invoke(input: RouteUpdateInput): ValidatorResult {
        val violations = buildList {
            EntityIdValidator().validate(input.id, EntityType.ROUTE)?.let(::add)

            if (!input.hasUpdates()) {
                add(ValidatorError.NoFieldsProvided(ValidatorField.ENTITY))
            }

            input.distanceKm?.let { rules.positive(it, ValidatorField.DISTANCE_KM)?.let(::add) }
            input.typicalDelayMin?.let {
                rules.nonNegative(it, ValidatorField.TYPICAL_DELAY_MIN)?.let(::add)
            }
            input.originWarehouse?.let {
                rules.requiredText(it.id, ValidatorField.ORIGIN_WAREHOUSE)?.let(::add)
            }
            input.destinationWarehouse?.let {
                rules.requiredText(it.id, ValidatorField.DESTINATION_WAREHOUSE)?.let(::add)
            }
        }
        return violations.toValidatorResult()
    }
}