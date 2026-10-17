package org.bytebloom.logiroute.data.source.csv

import org.bytebloom.logiroute.data.raw.WarehouseRaw

interface WarehouseDataSource {
    suspend fun loadAll(): List<WarehouseRaw>
    suspend fun getById(): List<WarehouseRaw>
    suspend fun create(): List<WarehouseRaw>
    suspend fun update(): List<WarehouseRaw>
    suspend fun delete(): List<WarehouseRaw>
}