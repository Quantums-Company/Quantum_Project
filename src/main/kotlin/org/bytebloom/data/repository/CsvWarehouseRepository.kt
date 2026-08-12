package org.bytebloom.data.repository

import org.bytebloom.data.csv.loadWarehouses
import org.bytebloom.data.raw.WarehouseRaw
import org.bytebloom.domain.repository.WarehouseRepository

class CsvWarehouseRepository(private val fileName: String = "warehouses.csv") : WarehouseRepository {
    override fun getAllWarehouses(): List<WarehouseRaw> {
        return loadWarehouses(fileName)
    }
}