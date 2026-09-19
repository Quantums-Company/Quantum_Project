package org.bytebloom.data.repository.csv

import org.bytebloom.data.mapper.toDomain
import org.bytebloom.data.source.WarehouseDataSource
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.util.Logger
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class CsvWarehouseRepository(
    private val csvWarehouseDataSource: WarehouseDataSource
) : WarehouseRepository {

    private var cachedWarehouses = listOf<Warehouse>()
    private var isLoaded = false
    private val mutex = Mutex() // Prevents duplicate concurrent loads

    suspend fun refresh() {
        mutex.withLock {
            Logger.info("Refreshing warehouses...")
            cachedWarehouses = csvWarehouseDataSource.loadAll().toDomain()
            isLoaded = true
        }
    }

    override suspend fun getAll(): List<Warehouse> {
        if (!isLoaded) {
            refresh()
        }
        return cachedWarehouses
    }

    override suspend fun getById(id: String): Warehouse? {
        return getAll().find { it.id == id }
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