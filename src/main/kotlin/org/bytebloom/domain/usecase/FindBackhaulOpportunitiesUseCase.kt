package org.bytebloom.domain.usecase

import org.bytebloom.domain.model.DomainGraph
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Vehicle

data class BackhaulOpportunity(
    val outboundHubId: String,
    val returnHubId: String,
    val deadheadCapacityKg: Double
)

class FindBackhaulOpportunitiesUseCase(
    private val graph: DomainGraph
) {

    operator fun invoke(): List<BackhaulOpportunity> {
        val packagesByCorridor = graph.packages
            .groupBy { it.originWarehouse.id to it.destinationWarehouse.id }
        val vehiclesByHub = graph.vehicles
            .groupBy { it.currentWarehouse.id }

        return graph.routes.mapNotNull { route ->
            val vehicles = vehiclesByHub[route.originWarehouse.id].orEmpty()
            val returning = packagesByCorridor[
                route.destinationWarehouse.id to route.originWarehouse.id
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
}