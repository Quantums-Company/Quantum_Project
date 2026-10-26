package org.bytebloom.logiroute.presentation.dependencies

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.bytebloom.logiroute.domain.routing.WarehouseGraph
import org.bytebloom.logiroute.domain.usecase.routes.GetAllRoutesUseCase
import org.bytebloom.logiroute.domain.usecase.routing.FindAllPairsShortestPathUseCase
import org.bytebloom.logiroute.domain.usecase.routing.FindFewestHopsRouteUseCase
import org.bytebloom.logiroute.domain.usecase.routing.VerifyHubLinkUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetAllWarehousesUseCase

data class RouteAnalysisDependencies(
    val getAllWarehouses: GetAllWarehousesUseCase,
    val getAllRoutes: GetAllRoutesUseCase,
    val fewestHopsFor: (WarehouseGraph) -> FindFewestHopsRouteUseCase,
    val verifyLinkFor: (WarehouseGraph) -> VerifyHubLinkUseCase,
    val allPairsFor: (WarehouseGraph) -> FindAllPairsShortestPathUseCase,
    val computeDispatcher: CoroutineDispatcher = Dispatchers.Default
)