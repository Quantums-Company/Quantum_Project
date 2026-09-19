package org.bytebloom.domain.usecase.crud.route

import org.bytebloom.domain.exception.EntityValidationException
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.validator.RouteIdValidator
import org.bytebloom.domain.validator.ValidationResult

class DeleteRouteUseCase(
    private val routeRepository: RouteRepository,
    private val validator: RouteIdValidator
) {
    suspend operator fun invoke(id: String): Boolean {
        when (val result = validator(id)) {
            is ValidationResult.Valid -> {
                return routeRepository.delete(id)
            }

            is ValidationResult.Invalid -> {
                throw EntityValidationException(result.violations)
            }
        }
    }
}