package org.bytebloom.domain.usecase

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.domain.routing.WarehouseGraphBuilder
import org.bytebloom.domain.routing.bfs.BidirectionalBreadthFirstRouter

class FindFewestHopsRouteUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val routeRepository: RouteRepository
) {

    operator fun invoke(startWarehouse: Warehouse, endWarehouse: Warehouse): List<Warehouse>? {
        val warehouseGraph = WarehouseGraphBuilder(
            warehouses = warehouseRepository.getAll(),
            routes = routeRepository.getAll()
        ).build()

        val bfsRouter = BidirectionalBreadthFirstRouter(warehouseGraph)
        return bfsRouter.findShortestPath(startWarehouse, endWarehouse)
    }

}