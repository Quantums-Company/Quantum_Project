package org.bytebloom.domain.routing

import org.bytebloom.domain.model.Warehouse

class WarehouseGraph {

    private val _adjacencyMap = mutableMapOf<Warehouse, MutableMap<Warehouse, Double>>()
    val adjacencyMap: Map<Warehouse, Map<Warehouse, Double>>
        get() = _adjacencyMap

    private val _reverseAdjacencyMap =
        mutableMapOf<Warehouse, MutableMap<Warehouse, Double>>()

    fun warehouses(): Set<Warehouse> =
        adjacencyMap.keys.toSet()

    fun addWarehouse(warehouse: Warehouse) {
        _adjacencyMap.putIfAbsent(warehouse, mutableMapOf())
        _reverseAdjacencyMap.putIfAbsent(warehouse, mutableMapOf()
        )
    }

    fun addRoute(originWarehouse: Warehouse, destinationWarehouse: Warehouse, distanceKm: Double) {
        addWarehouse(originWarehouse)
        addWarehouse(destinationWarehouse)

        _adjacencyMap.getValue(originWarehouse)[destinationWarehouse] = distanceKm
        _reverseAdjacencyMap.getValue(destinationWarehouse)[originWarehouse] = distanceKm
    }

    fun neighbors(warehouse: Warehouse): Map<Warehouse, Double> =
        adjacencyMap[warehouse]?.toMap()
            ?: emptyMap()

    fun predecessors(
        warehouse: Warehouse
    ): Map<Warehouse, Double> =
        _reverseAdjacencyMap[warehouse]?.toMap()
            ?: emptyMap()

    fun containsWarehouse(warehouse: Warehouse): Boolean = warehouse in _adjacencyMap
}