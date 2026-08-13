package org.bytebloom.domain.routing.bfs

class WarehouseGraph {

    private val _adjacencyMap = mutableMapOf<String, MutableMap<String, Double>>()
    val adjacencyMap: Map<String, Map<String, Double>>
        get() = _adjacencyMap

    fun addWarehouse(warehouseId: String) {
        _adjacencyMap.putIfAbsent(warehouseId, mutableMapOf())
    }

    fun addRoute(originId: String, destinationId: String, distanceKm: Double = 1.0) {
        addWarehouse(originId)
        addWarehouse(destinationId)

        _adjacencyMap.getValue(originId)[destinationId] = distanceKm
        _adjacencyMap.getValue(destinationId)[originId] = distanceKm
    }

    fun neighbors(warehouseId: String): Map<String, Double> {
        return _adjacencyMap[warehouseId] ?: emptyMap()
    }

    fun containsWarehouse(warehouseId: String): Boolean {
        return warehouseId in _adjacencyMap
    }
}