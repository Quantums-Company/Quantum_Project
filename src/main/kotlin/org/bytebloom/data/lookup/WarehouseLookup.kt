package org.bytebloom.data.lookup

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.util.Logger

fun Map<String, Warehouse>.findWarehouse(
    warehouseId: String,
    owner: String,
    ownerId: String
): Warehouse? {
    val warehouse = this[warehouseId]

    if (warehouse == null) {
        Logger.warning(
            "$owner '$ownerId' references unknown warehouse '$warehouseId'."
        )
    }

    return warehouse
}