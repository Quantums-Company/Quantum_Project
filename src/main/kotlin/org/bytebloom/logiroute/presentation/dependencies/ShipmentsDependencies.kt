package org.bytebloom.logiroute.presentation.dependencies

import org.bytebloom.logiroute.domain.routing.WarehouseGraph
import org.bytebloom.logiroute.domain.usecase.dispatch.ReroutePackageUseCase
import org.bytebloom.logiroute.domain.usecase.packages.GetAllPackagesUseCase
import org.bytebloom.logiroute.domain.usecase.routes.GetAllRoutesUseCase
import org.bytebloom.logiroute.domain.usecase.shipment.AdvanceShipmentStateUseCase
import org.bytebloom.logiroute.domain.usecase.shipment.EstimateShipmentDeliveryUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetAllWarehousesUseCase

data class ShipmentsDependencies(
    val getAllPackages: GetAllPackagesUseCase,
    val getAllWarehouses: GetAllWarehousesUseCase,
    val getAllRoutes: GetAllRoutesUseCase,
    val advanceShipmentState: AdvanceShipmentStateUseCase,
    val reroutePackage: ReroutePackageUseCase,
    val estimateFor: (WarehouseGraph) -> EstimateShipmentDeliveryUseCase
)