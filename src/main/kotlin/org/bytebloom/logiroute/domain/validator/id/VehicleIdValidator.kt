package org.bytebloom.logiroute.domain.validator.id

import org.bytebloom.logiroute.domain.model.EntityType
import org.bytebloom.logiroute.domain.model.validation.ValidatorResult

class VehicleIdValidator {
    private val entityIdValidator = EntityIdValidator()
    operator fun invoke(id: String): ValidatorResult = entityIdValidator(id, EntityType.VEHICLE)
}