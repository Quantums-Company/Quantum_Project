package org.bytebloom.domain.usecase.queries.shipment

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.usecase.queries.routing.FindOptimalPathUseCase

class EstimateShipmentDeliveryUseCase(
    private val packageRepository: PackageRepository,
    private val routeRepository: RouteRepository,
    private val findOptimalPath: FindOptimalPathUseCase
) {

    operator fun invoke(packageId: String): Double? {

        val packageData = packageRepository
            .getAll()
            .find {
                it.id.equals(packageId, ignoreCase = true)
            }
            ?: return null

        val path = findOptimalPath(
            packageData.originWarehouse,
            packageData.destinationWarehouse
        )
            ?: return null

        return calculateEstimatedTime(path)
    }

    private fun calculateEstimatedTime(
        path: List<Warehouse>
    ): Double? {

        val routeByCorridor = routeRepository.getAll().associateBy {
            it.originWarehouse.id to it.destinationWarehouse.id
        }

        var totalDelay = 0.0

        for ((origin, destination) in path.zipWithNext()) {
            val route = routeByCorridor[
                origin.id to destination.id
            ] ?: return null

            totalDelay += route.typicalDelayMin
        }

        return totalDelay
    }
}