package org.bytebloom.domain.usecase.required

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.routing.bfs.BidirectionalBreadthFirstRouter

class FindFewestHopsRouteUseCase(
    private val bfsRouter: BidirectionalBreadthFirstRouter
) {

    operator fun invoke(startWarehouse: Warehouse, endWarehouse: Warehouse): List<Warehouse>? {
        return bfsRouter.findShortestPath(startWarehouse, endWarehouse)
    }

}