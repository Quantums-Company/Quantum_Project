package org.bytebloom.domain.usecase.crud.route

import org.bytebloom.domain.repository.RouteRepository

class DeleteRouteUseCase(
    private val routeRepository: RouteRepository
) {
    operator fun invoke(id: String): Boolean {
        return routeRepository.delete(id)
    }
}