package org.bytebloom.data.mapper

import org.bytebloom.data.raw.WarehouseRaw
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.util.Logger

object DomainGraphBuilder {

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

    private fun buildWarehouses(
        warehouseRaws: List<WarehouseRaw>
    ): Map<String, Warehouse> {

        return warehouseRaws.associateBy(
            keySelector = { it.id },
            valueTransform = { raw ->
                Warehouse(
                    raw.id,
                    raw.name,
                    raw.regionalZone,
                    raw.longitude,
                    raw.latitude,
                )
            }
        )
    }
}