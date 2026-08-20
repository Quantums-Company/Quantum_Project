package org.bytebloom.data.mapper

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.util.Logger

class WarehouseReferenceMapper(
    private val warehousesById: Map<String, Warehouse>
) {

    fun map(
        warehouseId: String,
        owner: String,
        ownerId: String
    ): Warehouse? {

        val warehouse = warehousesById[warehouseId]

        if (warehouse == null) {
            Logger.warning(
                "$owner '$ownerId' references unknown warehouse '$warehouseId'."
            )
        }

        return warehouse
    }
}