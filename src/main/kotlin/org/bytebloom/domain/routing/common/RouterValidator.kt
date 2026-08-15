package org.bytebloom.domain.routing.common

import org.bytebloom.domain.routing.WarehouseGraph

open class RouterValidator (private val graph: WarehouseGraph){
    protected fun areValidWarehouses(
        startId: String,
        endId: String
    ): Boolean =
        graph.containsWarehouse(startId) &&
                graph.containsWarehouse(endId)


    protected fun reconstructPath(
        parent: Map<String, String>,
        endId: String
    ): List<String> {

        val path = mutableListOf<String>()
        var current: String? = endId

        while (current != null) {
            path.add(current)
            current = parent[current]
        }

        return path.asReversed()
    }
}