package org.bytebloom.logiroute.presentation

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.bytebloom.logiroute.domain.routing.WarehouseGraphBuilder
import org.bytebloom.logiroute.domain.usecase.routes.GetAllRoutesUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetAllWarehousesUseCase

/** Loads warehouses and routes in parallel and builds the graph. */
internal class RoutingDataLoader(
    private val getAllWarehouses: GetAllWarehousesUseCase,
    private val getAllRoutes: GetAllRoutesUseCase
) {
    suspend fun load(): RoutingData = coroutineScope {
        val warehousesRequest = async { getAllWarehouses() }
        val routesRequest = async { getAllRoutes() }
        val warehouses = warehousesRequest.await()

        val build = WarehouseGraphBuilder(warehouses, routesRequest.await()).build()
        RoutingData(warehouses, build.graph, build.skippedRouteIds)
    }
}