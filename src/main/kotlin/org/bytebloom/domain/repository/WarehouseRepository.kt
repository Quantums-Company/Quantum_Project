package org.bytebloom.domain.repository

import org.bytebloom.domain.model.Warehouse

interface WarehouseRepository {
    fun getAll(): List<Warehouse>
    fun getById(id: String): Warehouse?
    fun create(warehouse: Warehouse): Warehouse
    fun update(warehouse: Warehouse): Warehouse
    fun delete(id: String): Boolean
}