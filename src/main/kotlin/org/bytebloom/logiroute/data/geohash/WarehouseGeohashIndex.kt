package org.bytebloom.logiroute.data.geohash

import org.bytebloom.logiroute.domain.model.Warehouse

class WarehouseGeohashIndex(
    private val geohashUtils: GeohashImpl,
    private val precision: Int = 7
) {
    private var index: Map<String, List<Warehouse>> = emptyMap()

    fun isEmpty(): Boolean = index.isEmpty()

    fun buildSpatialIndex(warehouses: List<Warehouse>) {
        index = warehouses.groupBy { w ->
            geohashUtils.encode(w.latitude, w.longitude, precision) }
    }

    fun addWarehouse(warehouse: Warehouse) {
        val hash = geohashUtils
            .encode(warehouse.latitude, warehouse.longitude, precision)

        val currentList = index[hash] ?: emptyList()
        val updatedList = currentList.filter { it.id != warehouse.id } + warehouse

        index = index + (hash to updatedList)
    }

    fun deleteWarehouse(warehouseId: String) {
        index = index.asSequence()
            .map { (geohash, warehouses) ->
                geohash to warehouses.filter { it.id != warehouseId }
            }
            .filter { (_, warehouses) ->
                warehouses.isNotEmpty()
            }
            .toMap()
    }

    fun queryNearby(lat: Double, lon: Double): List<Warehouse> {
        val center = geohashUtils.encode(lat, lon, precision)
        val allTargetHashes = geohashUtils.neighbors(center).values + center
        return allTargetHashes.flatMap { hash -> index[hash] ?: emptyList() }
    }
}