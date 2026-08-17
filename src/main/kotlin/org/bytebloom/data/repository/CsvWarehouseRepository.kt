package org.bytebloom.data.repository

import org.bytebloom.data.csv.loadWarehouses
import org.bytebloom.data.raw.WarehouseRaw
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.WarehouseRepository

class CsvWarehouseRepository : WarehouseRepository {
    override fun getAllWarehouses(): List<Warehouse> {
        return loadWarehouses()
    }
}