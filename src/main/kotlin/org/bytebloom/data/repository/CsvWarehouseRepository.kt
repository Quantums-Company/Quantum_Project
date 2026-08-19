package org.bytebloom.data.repository

import org.bytebloom.data.csv.DEFAULT_CSV_DIRECTORY
import org.bytebloom.data.csv.loadWarehouses
import org.bytebloom.data.mapper.toDomain
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.WarehouseRepository

class CsvWarehouseRepository (private val csvDirectory: String = DEFAULT_CSV_DIRECTORY)
    : WarehouseRepository {
    override fun getAll(): List<Warehouse> =
        loadWarehouses(csvDirectory).map{it.toDomain()}
}