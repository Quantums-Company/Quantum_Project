package org.bytebloom.domain.usecase.crud.route

import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.validator.create.CreateRouteValidator
import org.bytebloom.domain.validation.ValidationResult
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.service.IdGenerator
import org.bytebloom.domain.validation.EntityType


class CreateRouteUseCase(
    private val routeRepository: RouteRepository,
    private val validator: CreateRouteValidator,
    private val idGenerator: IdGenerator
) {
    suspend operator fun invoke(distanceKm: Double, typicalDelayMin: Int, originWarehouse: Warehouse, destinationWarehouse: Warehouse): Route {

        val route = Route(
            id = idGenerator.next(EntityType.ROUTE.idPrefix),
            distanceKm = distanceKm,
            typicalDelayMin = typicalDelayMin,
            originWarehouse = originWarehouse,
            destinationWarehouse = destinationWarehouse
        )

        return when (val result = validator(route)) {
            is ValidationResult.Valid -> { routeRepository.create(route) }

            is ValidationResult.Invalid -> { throw EntityValidationException(result.violations) }
        }
    }
}