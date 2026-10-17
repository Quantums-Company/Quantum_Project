package org.bytebloom.logiroute.domain.usecase.crud.routes

import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.repository.RouteRepository
import org.bytebloom.logiroute.domain.model.validation.ValidatorResult
import org.bytebloom.logiroute.domain.validator.id.RouteIdValidator

class DeleteRouteUseCase(
    private val routeRepository: RouteRepository,
    private val validator: RouteIdValidator
) {
    suspend operator fun invoke(id: String): Boolean =
        when (val result = validator(id)) {
            is ValidatorResult.Valid -> routeRepository.delete(id)
            is ValidatorResult.Invalid -> throw EntityValidationException(result.violations)
        }
}