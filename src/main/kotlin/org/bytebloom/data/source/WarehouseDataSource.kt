package org.bytebloom.data.source

import org.bytebloom.data.raw.WarehouseRaw

interface WarehouseDataSource {
    fun loadAll(): List<WarehouseRaw>
}