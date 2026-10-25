package org.bytebloom.logiroute.domain.repository

import org.bytebloom.logiroute.domain.model.Warehouse

interface SpatialWarehouseRepository {
    suspend fun getNearby(lat: Double, lon: Double): List<Warehouse>

    suspend fun indexAll(warehouses: List<Warehouse>)

    suspend fun add(warehouse: Warehouse)

    suspend fun remove(warehouseId: String)
}