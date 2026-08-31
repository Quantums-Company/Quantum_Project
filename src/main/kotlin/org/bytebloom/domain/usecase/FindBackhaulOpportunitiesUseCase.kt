package org.bytebloom.domain.usecase

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.repository.VehicleRepository

data class BackhaulOpportunity(
    val outboundHubId: String,
    val returnHubId: String,
    val deadheadCapacityKg: Double
)

class FindBackhaulOpportunitiesUseCase(
    private val routeRepository: RouteRepository,
    private val vehicleRepository: VehicleRepository,
    private val packageRepository: PackageRepository
) {

    operator fun invoke(): List<BackhaulOpportunity> {
        val routes = routeRepository.getAll()
        val vehicles = vehicleRepository.getAll()
        val packages = packageRepository.getAll()

        val vehiclesByHub =
            vehicles.groupBy {
                it.currentWarehouse.id
            }

        val packagesByCorridor =
            packages.groupBy {
                it.originWarehouse.id to
                        it.destinationWarehouse.id
            }

        return routes.mapNotNull { route ->
            val vehicles = vehicleList(vehiclesByHub, route)
            val returning = packagesByCorridor[
                getReturnCorridor(route)
            ].orEmpty()
            if (vehicles.isEmpty() || returning.isEmpty()) {
                null
            } else {
                BackhaulOpportunity(
                    outboundHubId = route.originWarehouse.id,
                    returnHubId = route.destinationWarehouse.id,
                    deadheadCapacityKg = (
                            vehicles.sumOf(Vehicle::maxCapacityKg) - returning.sumOf(Package::weight)
                            ).coerceAtLeast(0.0)
                )
            }
        }
            .distinctBy { it.outboundHubId to it.returnHubId }
            .sortedByDescending { it.deadheadCapacityKg }
    }

    private fun vehicleList(
        vehiclesByHub: Map<String, List<Vehicle>>,
        route: Route
    ) = vehiclesByHub[route.originWarehouse.id].orEmpty()

    private fun getReturnCorridor(route: Route) = route.destinationWarehouse.id to route.originWarehouse.id
}