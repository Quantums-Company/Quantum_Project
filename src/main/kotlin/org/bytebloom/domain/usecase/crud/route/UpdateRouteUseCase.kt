package org.bytebloom.domain.usecase.crud.route

import org.bytebloom.domain.model.Route
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.validator.UpdateRouteValidator
import org.bytebloom.domain.validator.ValidationResult

class UpdateRouteUseCase(
    private val routeRepository: RouteRepository,
    private val validator: UpdateRouteValidator
) {
    suspend operator fun invoke(route: Route): Route {

        when (val result = validator(route)) {
            is ValidationResult.Valid -> {
                return routeRepository.update(route)
            }

            is ValidationResult.Invalid -> {
                throw IllegalArgumentException(
                    result.violations.joinToString(", ")
                )
            }
        }
    }
}