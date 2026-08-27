package org.bytebloom.domain.usecase

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.routing.dijkstra.DijkstraRouter

class FindOptimalPathUseCase(
    private val dijkstraRouter: DijkstraRouter
) {

    operator fun invoke(startWarehouse: Warehouse, endWarehouse: Warehouse): List<Warehouse>? {
        return dijkstraRouter.findShortestPath(startWarehouse, endWarehouse)
    }

}
