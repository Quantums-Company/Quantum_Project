package org.bytebloom.logiroute.domain.routing.common

import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.routing.WarehouseGraph

open class RouterValidator (private val graph: WarehouseGraph){
    protected fun areValidWarehouses(
        startWarehouse: Warehouse,
        endWarehouse: Warehouse
    ): Boolean =
        graph.containsWarehouse(startWarehouse) &&
                graph.containsWarehouse(endWarehouse)


    protected fun reconstructPath(
        parent: Map<Warehouse, Warehouse>,
        endWarehouse: Warehouse
    ): List<Warehouse> {

        val path = mutableListOf<Warehouse>()
        var current: Warehouse? = endWarehouse

        while (current != null) {
            path.add(current)
            current = parent[current]
        }

        return path.asReversed()
    }
}