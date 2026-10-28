package org.bytebloom.logiroute.presentation.dependencies

import org.bytebloom.logiroute.domain.tree.hub.HubTree
import org.bytebloom.logiroute.domain.tree.hub.HubTreeBuilder
import org.bytebloom.logiroute.domain.usecase.warehouses.CreateWarehouseUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.DeleteWarehouseUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetAllWarehousesUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetWarehouseByIdUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetWarehouseLoadFactorUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetWarehouseReportUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.TraceHubLineageUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.UpdateWarehouseUseCase

data class WarehousesManagementDependencies(
    val getAllWarehouses: GetAllWarehousesUseCase,
    val getWarehouseById: GetWarehouseByIdUseCase,
    val createWarehouse: CreateWarehouseUseCase,
    val updateWarehouse: UpdateWarehouseUseCase,
    val deleteWarehouse: DeleteWarehouseUseCase,
    val getWarehouseReport: GetWarehouseReportUseCase,
    val getWarehouseLoadFactor: GetWarehouseLoadFactorUseCase,
    val hubTreeBuilder: HubTreeBuilder,
    val lineageFor: (HubTree) -> TraceHubLineageUseCase
)