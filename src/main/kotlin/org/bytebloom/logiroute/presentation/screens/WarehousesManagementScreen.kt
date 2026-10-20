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

    /** [loadFactor] is cargo weight divided by the capacity of the stationed vehicles (0 when no vehicle is stationed). */
    fun showLoadFactor(warehouse: Warehouse, loadFactor: Double)

    /** The warehouse first, then its regional hub, then the global hub. Empty when the warehouse is not in the tree. */
    fun showHubLineage(warehouse: Warehouse, lineage: List<Warehouse>)
}
