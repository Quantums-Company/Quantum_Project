package org.bytebloom.domain.usecase.crud.route

import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.exception.ResourceNotFoundException
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.validation.input.RouteUpdateInput
import org.bytebloom.domain.validator.update.UpdateRouteValidator
import org.bytebloom.domain.validation.ValidationResult

class UpdateRouteUseCase(
    private val routeRepository: RouteRepository,
    private val validator: UpdateRouteValidator
) {
    suspend operator fun invoke(input: RouteUpdateInput): Route {
        when (val result = validator(input)) {
            is ValidationResult.Valid -> {
                val existing = routeRepository.getById(input.id)
                    ?: throw ResourceNotFoundException("Route '${input.id}' was not found")

                val updated = Route(
                    id = existing.id,
                    distanceKm = input.distanceKm ?: existing.distanceKm,
                    typicalDelayMin = input.typicalDelayMin ?: existing.typicalDelayMin,
                    originWarehouse = input.originWarehouse ?: existing.originWarehouse,
                    destinationWarehouse = input.destinationWarehouse ?: existing.destinationWarehouse
                )

                return routeRepository.update(updated)
            }

            is ValidationResult.Invalid -> {
                throw EntityValidationException(result.violations)
            }
        }
    }
}