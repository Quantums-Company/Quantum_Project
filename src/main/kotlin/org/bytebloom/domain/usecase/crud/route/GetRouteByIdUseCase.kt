package org.bytebloom.domain.usecase.crud.route

import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.validation.ValidationResult
import org.bytebloom.domain.validator.id.RouteIdValidator

class GetRouteByIdUseCase(
    private val routeRepository: RouteRepository,
    private val validator: RouteIdValidator
) {
    suspend operator fun invoke(id: String): Route? =
        when (val result = validator(id)) {
            is ValidationResult.Valid ->  routeRepository.getById(id)
            is ValidationResult.Invalid -> throw EntityValidationException(result.violations)
        }
}
