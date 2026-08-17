package org.bytebloom.domain.repository

import org.bytebloom.domain.model.Warehouse

interface WarehouseRepository {
    fun getAllWarehouses (): List<Warehouse>
}