package org.bytebloom.domain.usecase.required

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.domain.routing.WarehouseGraphBuilder
import org.bytebloom.domain.routing.dijkstra.DijkstraRouter

class FindOptimalPathUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val routeRepository: RouteRepository
) {

    operator fun invoke(startWarehouse: Warehouse, endWarehouse: Warehouse): List<Warehouse>? {
        val warehouseGraph = WarehouseGraphBuilder(
            warehouses = warehouseRepository.getAll(),
            routes = routeRepository.getAll()
        ).build()

        val dijkstraRouter = DijkstraRouter(warehouseGraph)
        return dijkstraRouter.findShortestPath(startWarehouse, endWarehouse)
    }

}
