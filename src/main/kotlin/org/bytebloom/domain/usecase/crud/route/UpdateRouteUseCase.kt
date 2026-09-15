package org.bytebloom.domain.usecase.crud.route

import org.bytebloom.domain.model.Route
import org.bytebloom.domain.repository.RouteRepository

class UpdateRouteUseCase(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(route: Route): Route {
        return routeRepository.update(route)
    }
}