package org.bytebloom.domain.routing

class WarehouseGraph {

    private val _adjacencyMap = mutableMapOf<String, MutableMap<String, Double>>()
    val adjacencyMap: Map<String, Map<String, Double>>
        get() = _adjacencyMap

    fun warehouseIds(): Set<String> =
        adjacencyMap.keys.toSet()

    fun addWarehouse(warehouseId: String) {
        _adjacencyMap.putIfAbsent(warehouseId, mutableMapOf())
    }

    fun addRoute(originId: String, destinationId: String, distanceKm: Double = 1.0) {
        addWarehouse(originId)
        addWarehouse(destinationId)

        _adjacencyMap.getValue(originId)[destinationId] = distanceKm
    }

    fun neighbors(warehouseId: String): Map<String, Double> =
        adjacencyMap[warehouseId]?.toMap()
            ?: emptyMap()

    fun containsWarehouse(warehouseId: String): Boolean = warehouseId in _adjacencyMap
}