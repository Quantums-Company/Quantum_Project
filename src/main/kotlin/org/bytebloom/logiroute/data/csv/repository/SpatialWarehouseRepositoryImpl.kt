package org.bytebloom.logiroute.data.csv.repository

import org.bytebloom.logiroute.data.geohash.WarehouseGeohashIndex
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.repository.SpatialWarehouseRepository
import org.bytebloom.logiroute.domain.repository.WarehouseRepository

class SpatialWarehouseRepositoryImpl(
    private val delegate: WarehouseRepository,
    private val spatialIndex: WarehouseGeohashIndex
) : SpatialWarehouseRepository {

    override suspend fun getNearby(lat: Double, lon: Double): List<Warehouse> {
        val spatialResults= spatialIndex.queryNearby(lat, lon)
        return spatialResults.takeIf { it.isNotEmpty() } ?: run {
            val currentWarehouses = delegate.getAll()
            currentWarehouses.takeIf { it.isNotEmpty() }?.let { warehouses ->
                indexAll(warehouses)
                spatialIndex.queryNearby(lat, lon)
            } ?: emptyList()
        }
    }

    override suspend fun indexAll(warehouses: List<Warehouse>) {
        return spatialIndex.buildSpatialIndex(warehouses)
    }

    override suspend fun add(warehouse: Warehouse) {
        spatialIndex.addWarehouse(warehouse)
    }

    override suspend fun remove(warehouseId: String) {
        spatialIndex.deleteWarehouse(warehouseId)
    }
}