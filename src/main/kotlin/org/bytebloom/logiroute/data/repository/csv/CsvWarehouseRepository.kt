package org.bytebloom.logiroute.data.repository.csv

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bytebloom.logiroute.data.mapper.toDomain
import org.bytebloom.logiroute.data.source.csv.WarehouseDataSource
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.repository.WarehouseRepository

class CsvWarehouseRepository(
    private val csvWarehouseDataSource: WarehouseDataSource
) : WarehouseRepository {

    private var cachedWarehouses: List<Warehouse>? = null
    private val mutex = Mutex()

    private suspend fun loadAll(): List<Warehouse> =
        csvWarehouseDataSource.loadAll().toDomain()

    suspend fun refresh(): List<Warehouse> =
        mutex.withLock {
            loadAll().also { warehouses ->
                cachedWarehouses = warehouses
            }
        }

    override suspend fun getAll(): List<Warehouse> {
        cachedWarehouses?.let { return it }

        return mutex.withLock {
            cachedWarehouses
                ?: loadAll().also { warehouses ->
                    cachedWarehouses = warehouses
                }
        }
    }

    override suspend fun getById(id: String): Warehouse? =
        getAll().find { it.id == id }

    override suspend fun create(warehouse: Warehouse): Warehouse =
        throw UnsupportedOperationException(
            "CSV repository is read-only. Create the warehouse through the remote repository."
        )

    override suspend fun update(warehouse: Warehouse): Warehouse =
        throw UnsupportedOperationException(
            "CSV repository is read-only. Update the warehouse through the remote repository."
        )

    override suspend fun delete(id: String): Boolean =
        throw UnsupportedOperationException(
            "CSV repository is read-only. Delete the warehouse through the remote repository."
        )
}