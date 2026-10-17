package org.bytebloom.logiroute.domain.usecase.crud.routes

import org.bytebloom.logiroute.domain.model.Route
import org.bytebloom.logiroute.domain.repository.RouteRepository

class GetAllRoutesUseCase(
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(): List<Route> =
        routeRepository.getAll()
}