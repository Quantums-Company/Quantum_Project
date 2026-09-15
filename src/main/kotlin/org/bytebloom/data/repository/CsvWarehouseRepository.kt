package org.bytebloom.data.repository

import org.bytebloom.data.mapper.toDomain
import org.bytebloom.data.source.WarehouseDataSource
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.util.Logger

class CsvWarehouseRepository (
    private val csvWarehouseDataSource: WarehouseDataSource
): WarehouseRepository {
    private var cachedWarehouses = listOf<Warehouse>()

    fun refresh(){
        cachedWarehouses = csvWarehouseDataSource.loadAll().toDomain()
    }

    init {
        Logger.info("Loading vehicles in init...")
        cachedWarehouses = csvWarehouseDataSource.loadAll().toDomain()
    }

    override suspend fun getAll(): List<Warehouse> = cachedWarehouses
    override suspend fun getById(id: String): Warehouse? {
        TODO("Not yet implemented")
    }

    override suspend fun create(warehouse: Warehouse): Warehouse {
        TODO("Not yet implemented")
    }

    override suspend fun update(warehouse: Warehouse): Warehouse {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: String): Boolean {
        TODO("Not yet implemented")
    }
}