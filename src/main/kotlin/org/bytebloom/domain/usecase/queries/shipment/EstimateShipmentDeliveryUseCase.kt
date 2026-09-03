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

        val routeByCorridor =
            routeRepository
                .getAll()
                .associateBy {
                    it.originWarehouse.id to
                            it.destinationWarehouse.id
                }

        val delays =
            path
                .zipWithNext()
                .map { (origin, destination) ->
                    routeByCorridor[
                        origin.id to destination.id
                    ]?.typicalDelayMin
                }

        return if (delays.any { it == null }) {
            null
        } else {
            delays.filterNotNull().sum().toDouble()
        }
    }
}