package org.bytebloom.domain.usecase.crud.route

import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.validator.CreateRouteValidator
import org.bytebloom.domain.validator.ValidationResult

class CreateRouteUseCase(
    private val routeRepository: RouteRepository,
    private val validator: CreateRouteValidator
) {
    suspend operator fun invoke(route: Route): Route {
        when (val result = validator(route)) {
            is ValidationResult.Valid -> {
                return routeRepository.create(route)
            }

            is ValidationResult.Invalid -> {
                throw EntityValidationException(result.violations)
            }
        }
    }
}