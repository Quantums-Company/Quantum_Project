package org.bytebloom.logiroute.presentation

import kotlinx.coroutines.CancellationException
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.routing.WarehouseGraph
import org.bytebloom.logiroute.domain.routing.WarehouseGraphBuilder
import org.bytebloom.logiroute.domain.usecase.routes.GetAllRoutesUseCase
import org.bytebloom.logiroute.domain.usecase.routing.FindOptimalPathUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetAllWarehousesUseCase
import org.bytebloom.logiroute.presentation.screen.RoutesManagementScreen

class RoutesManagementPresenter(
    private val getAllWarehouses: GetAllWarehousesUseCase,
    private val getAllRoutes: GetAllRoutesUseCase,
    private val findOptimalPath: (WarehouseGraph) -> FindOptimalPathUseCase,
    private val screen: RoutesManagementScreen
) {
    private var warehouses: List<Warehouse> = emptyList()

    suspend fun loadWarehouses() {
        screen.showLoadingIndicator(true)
        try {
            warehouses = getAllWarehouses()
            screen.showWarehouses(warehouses)
            val build = WarehouseGraphBuilder(warehouses, getAllRoutes()).build()
            screen.showSkippedRoutes(build.skippedRouteIds)
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            screen.showErrorMessage(error.userMessage())
        } finally {
            screen.showLoadingIndicator(false)
        }
    }

    suspend fun calculateRoute(originId: String, destinationId: String) {
        screen.showLoadingIndicator(true)
        try {
            if (warehouses.isEmpty()) warehouses = getAllWarehouses()
            val origin = warehouses.firstOrNull { it.id.equals(originId.trim(), true) }
                ?: return screen.showErrorMessage("Origin warehouse '$originId' was not found.")
            val destination = warehouses.firstOrNull { it.id.equals(destinationId.trim(), true) }
                ?: return screen.showErrorMessage("Destination warehouse '$destinationId' was not found.")
            val graph = WarehouseGraphBuilder(warehouses, getAllRoutes()).build()
            val path = findOptimalPath(graph.graph)(origin, destination)
            val distance = path.orEmpty().zipWithNext().sumOf { (from, to) ->
                graph.graph.adjacencyMap[from]?.get(to) ?: 0.0
            }
            screen.showRoute(origin, destination, path, distance)
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            screen.showErrorMessage(error.userMessage())
        } finally {
            screen.showLoadingIndicator(false)
        }
    }
}
