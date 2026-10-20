package org.bytebloom.logiroute.data.csv.datasource

import org.bytebloom.logiroute.data.csv.raw.WarehouseRaw

interface WarehouseDataSource {
    suspend fun loadAll(): List<WarehouseRaw>
}