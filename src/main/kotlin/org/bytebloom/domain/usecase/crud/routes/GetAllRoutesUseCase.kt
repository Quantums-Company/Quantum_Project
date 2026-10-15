package org.bytebloom.domain.usecase.crud.routes

import org.bytebloom.domain.model.Route
import org.bytebloom.domain.repository.RouteRepository

class GetAllRoutesUseCase(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(): List<Route> =
        routeRepository.getAll()
}