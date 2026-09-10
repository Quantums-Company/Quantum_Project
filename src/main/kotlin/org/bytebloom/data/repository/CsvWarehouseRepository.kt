package org.bytebloom.data.repository

import org.bytebloom.data.csv.DEFAULT_CSV_DIRECTORY
import org.bytebloom.data.csv.loadWarehouses
import org.bytebloom.data.mapper.toDomain
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.util.Logger

class CsvWarehouseRepository (private val csvDirectory: String = DEFAULT_CSV_DIRECTORY)
    : WarehouseRepository {
    private var cachedWarehouses = listOf<Warehouse>()

    fun refresh(){
        cachedWarehouses = loadWarehouses(csvDirectory).toDomain()
    }

    init {
        Logger.info("Loading vehicles in init...")
        cachedWarehouses = loadWarehouses(csvDirectory).toDomain()
    }

    override fun getAll(): List<Warehouse> = cachedWarehouses
}