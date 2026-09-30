package org.bytebloom.domain.usecase.crud.route

import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.validator.create.CreateRouteValidator
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.service.IdGenerator
import org.bytebloom.domain.model.EntityType


class CreateRouteUseCase(
    private val routeRepository: RouteRepository,
    private val validator: CreateRouteValidator,
    private val idGenerator: IdGenerator
) {
    suspend operator fun invoke(
        distanceKm: Double,
        typicalDelayMin: Int,
        originWarehouse: Warehouse,
        destinationWarehouse: Warehouse
    ): Route {

        val route = Route(
            id = idGenerator.next(EntityType.ROUTE),
            distanceKm = distanceKm,
            typicalDelayMin = typicalDelayMin,
            originWarehouse = originWarehouse,
            destinationWarehouse = destinationWarehouse
        )

        return when (val result = validator(route)) {
            is ValidatorResult.Valid -> { routeRepository.create(route) }

            is ValidatorResult.Invalid -> { throw EntityValidationException(result.violations) }
        }
    }
}