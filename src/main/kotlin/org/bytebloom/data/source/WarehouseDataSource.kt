package org.bytebloom.data.source

import org.bytebloom.data.raw.WarehouseRaw

interface WarehouseDataSource {
    suspend fun loadAll(): List<WarehouseRaw>
    suspend fun getById(): List<WarehouseRaw>
    suspend fun create(): List<WarehouseRaw>
    suspend fun update(): List<WarehouseRaw>
    suspend fun delete(): List<WarehouseRaw>
}