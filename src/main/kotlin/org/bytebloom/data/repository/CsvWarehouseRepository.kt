package org.bytebloom.data.repository

import org.bytebloom.data.csv.loadWarehouses
import org.bytebloom.data.mapper.toDomain
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.util.Logger

class CsvWarehouseRepository : WarehouseRepository {
    private fun findWarehouse(
        warehouseMap: Map<String, Warehouse>,
        warehouseId: String,
        owner: String
    ): Warehouse? {

        val warehouse = warehouseMap[warehouseId]

        if (warehouse == null) {
            Logger.warning(
                "$owner references unknown warehouse '$warehouseId'."
            )
        }

        return warehouse
    }

    override fun getAllWarehouses(): List<Warehouse> {
        return loadWarehouses().map{it.toDomain()}
    }
}