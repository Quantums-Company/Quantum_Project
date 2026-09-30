package org.bytebloom.domain.usecase.crud.route

import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.validator.id.RouteIdValidator

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