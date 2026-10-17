package org.bytebloom.logiroute.data.mapper

import org.bytebloom.logiroute.domain.model.Warehouse

class WarehouseReferenceMapper(
    private val warehousesById: Map<String, Warehouse>
) {

    fun map(
        warehouseId: String
    ): Warehouse? = warehousesById[warehouseId]

}