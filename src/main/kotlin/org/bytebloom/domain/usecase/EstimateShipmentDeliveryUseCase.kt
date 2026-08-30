package org.bytebloom.domain.usecase

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.domain.routing.WarehouseGraphBuilder
import org.bytebloom.domain.routing.dijkstra.DijkstraRouter

class EstimateShipmentDeliveryUseCase(
    private val packageRepository: PackageRepository,
    private val routeRepository: RouteRepository,
    private val warehouseRepository: WarehouseRepository
) {

    operator fun invoke(packageId: String): Double? {

        val packageData = packageRepository
            .getAll()
            .find { it.id.equals(packageId, ignoreCase = true) }
            ?: return null

        val warehouseGraph = WarehouseGraphBuilder(
            warehouses = warehouseRepository.getAll(),
            routes = routeRepository.getAll()
        ).build()

        val dijkstraRouter = DijkstraRouter(warehouseGraph)

        val path = dijkstraRouter.findShortestPath(
            packageData.originWarehouse,
            packageData.destinationWarehouse
        ) ?: return null

        return calculateEstimatedTime(path)
    }

    private fun calculateEstimatedTime(
        path: List<Warehouse>
    ): Double {

        val routes = routeRepository.getAll()

        return path
            .zipWithNext()
            .sumOf { (origin, destination) ->
                routes
                    .find {
                        it.originWarehouse.id == origin.id &&
                                it.destinationWarehouse.id == destination.id
                    }
                    ?.typicalDelayMin
                    ?.toDouble()
                    ?: 0.0
            }
    }
}