package org.bytebloom.logiroute.domain.usecase.routes

import org.bytebloom.logiroute.domain.model.Route
import org.bytebloom.logiroute.domain.repository.RouteRepository
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.service.IdGenerator
import org.bytebloom.logiroute.domain.model.EntityType

class CreateRouteUseCase(
    private val routeRepository: RouteRepository,
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

        return routeRepository.create(route)
    }
}