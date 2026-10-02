package org.bytebloom.data.mapper

import org.bytebloom.domain.model.Warehouse

class WarehouseReferenceMapper(
    private val warehousesById: Map<String, Warehouse>
) {

    fun map(
        warehouseId: String
    ): Warehouse? = warehousesById[warehouseId]

}