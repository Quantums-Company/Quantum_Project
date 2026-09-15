package org.bytebloom.domain.usecase.crud.route

import org.bytebloom.domain.model.Route
import org.bytebloom.domain.repository.RouteRepository

class GetRouteByIdUseCase(
    private val routeRepository: RouteRepository
) {
    operator fun invoke(id: String): Route? {
        return routeRepository.getById(id)
    }
}