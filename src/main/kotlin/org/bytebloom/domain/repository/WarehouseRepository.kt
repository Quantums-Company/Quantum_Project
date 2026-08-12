package org.bytebloom.domain.repository

import org.bytebloom.data.raw.WarehouseRaw

interface WarehouseRepository {
    fun getAllWarehouses (): List<WarehouseRaw>
}