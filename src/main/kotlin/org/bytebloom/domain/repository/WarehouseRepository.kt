package org.bytebloom.domain.repository

import org.bytebloom.domain.model.Warehouse

interface WarehouseRepository {
    suspend fun getAll(): List<Warehouse>
    suspend fun getById(id: String): Warehouse?
    suspend fun create(warehouse: Warehouse): Warehouse
    suspend fun update(warehouse: Warehouse): Warehouse
    suspend fun delete(id: String): Boolean
}