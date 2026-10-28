package org.bytebloom.logiroute.presentation.screens

import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.result.WarehouseReport

/** What the warehouse management screen must be able to show. Implemented by the UI. */
interface WarehousesManagementScreen : BaseScreen {

    fun showWarehouses(warehouses: List<Warehouse>)

    fun showWarehouse(warehouse: Warehouse)

    fun showWarehouseNotFound(id: String)

    fun showWarehouseCreated(warehouse: Warehouse)

    fun showWarehouseUpdated(warehouse: Warehouse)

    fun showWarehouseDeleted(id: String, wasDeleted: Boolean)

    fun showReport(report: WarehouseReport)

    fun showLoadFactor(warehouse: Warehouse, loadFactor: Double)

    fun showHubLineage(warehouse: Warehouse, lineage: List<Warehouse>)
}
