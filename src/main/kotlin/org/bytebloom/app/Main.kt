package org.bytebloom.app

import org.bytebloom.data.repository.CsvPackageRepository
import org.bytebloom.data.repository.CsvRouteRepository
import org.bytebloom.data.repository.CsvVehicleRepository
import org.bytebloom.data.repository.CsvWarehouseRepository
import org.bytebloom.domain.graph.DomainGraph
import org.bytebloom.domain.graph.DomainGraphBuilder
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.pricing.core.BasePackageComponent
import org.bytebloom.domain.pricing.core.PackageComponent
import org.bytebloom.domain.pricing.core.RoutePricingEngine
import org.bytebloom.domain.pricing.decorator.ColdChainDecorator
import org.bytebloom.domain.pricing.decorator.ExpressInsuranceDecorator
import org.bytebloom.domain.pricing.decorator.FragileHandlingDecorator
import org.bytebloom.domain.pricing.strategy.EcoStrategy
import org.bytebloom.domain.printing.printRouteComparison
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.domain.routing.WarehouseGraphBuilder
import org.bytebloom.domain.routing.bfs.BfsBenchmark
import org.bytebloom.domain.routing.bfs.BidirectionalBreadthFirstRouter
import org.bytebloom.domain.routing.bfs.UnidirectionalBreadthFirstRouter
import org.bytebloom.domain.routing.dijkstra.DijkstraRouter
import org.bytebloom.util.Logger

private const val DEMO_ROUTE_ORIGIN_ID = "WH-031"
private const val DEMO_ROUTE_DESTINATION_ID = "WH-091"

private const val FRAGILE_HANDLING_SURCHARGE = 10.0
private const val COLD_CHAIN_MULTIPLIER = 1.25
private const val EXPRESS_INSURANCE_SURCHARGE = 20.0

fun main() {
    val graph = buildDomainGraph()

//    runPricingDemo(graph)
    runRoutingDemo(graph)
}

private fun buildDomainGraph(): DomainGraph {
    val warehouseRepo: WarehouseRepository = CsvWarehouseRepository()
    val warehousesById = warehouseRepo.getAll().associateBy { it.id }

    val packageRepo: PackageRepository = CsvPackageRepository(warehousesById)
    val routeRepo: RouteRepository = CsvRouteRepository(warehousesById)
    val vehicleRepo: VehicleRepository = CsvVehicleRepository(warehousesById)

    return DomainGraphBuilder.buildGraph(
        warehouseRepo,
        packageRepo,
        routeRepo,
        vehicleRepo
    )
}

private fun runPricingDemo(graph: DomainGraph) {
        val packageData = Package(
            "PKG-001",
            12.5,
            Priority.STANDARD,
            graph.warehouses[0],
            graph.warehouses[1],
        )

    val pricingEngine = RoutePricingEngine(EcoStrategy())

    var service: PackageComponent = BasePackageComponent(
        packageData,
        graph.routes,
        pricingEngine
    )

    Logger.info("\n==============================================")
    Logger.info("           PACKAGE PRICING REPORT             ")
    Logger.info("==============================================")
    Logger.info("Package ID      : ${service.getPackage().id}")
    Logger.info("Origin          : ${service.getPackage().originWarehouse.id}")
    Logger.info("Destination     : ${service.getPackage().destinationWarehouse.id}")
    Logger.info("Priority        : ${service.getPackage().priority}")
    Logger.info("----------------------------------------------")

    Logger.info("Base Rate       : %.2f".format(service.getTransitRate()))

    service = FragileHandlingDecorator(service, FRAGILE_HANDLING_SURCHARGE)
    Logger.info("After Fragile   : %.2f".format(service.getTransitRate()))

    service = ColdChainDecorator(service, COLD_CHAIN_MULTIPLIER)
    Logger.info("After Cold Chain: %.2f".format(service.getTransitRate()))

    service = ExpressInsuranceDecorator(service, EXPRESS_INSURANCE_SURCHARGE)
    val finalRate = service.getTransitRate()

    if (finalRate == null) {
        Logger.warning(
            "No direct route found for package ${service.getPackage().id}. " +
                    "Transit rate cannot be calculated."
        )
    } else {
        Logger.info("Final Transit Rate : %.2f".format(finalRate))
    }

    Logger.info("==============================================")
}

private fun runRoutingDemo(graph: DomainGraph) {
    val warehousesById = graph.warehouses.associateBy { it.id }

    val start = warehousesById[DEMO_ROUTE_ORIGIN_ID]
    val destination = warehousesById[DEMO_ROUTE_DESTINATION_ID]

    if (start == null || destination == null) {
        Logger.warning(
            "Cannot run routing demo: warehouse '$DEMO_ROUTE_ORIGIN_ID' or " +
                    "'$DEMO_ROUTE_DESTINATION_ID' was not found."
        )
        return
    }

    val warehouseGraph = WarehouseGraphBuilder(
        warehouses = graph.warehouses,
        routes = graph.routes
    ).build()

    val unidirectionalRouter = UnidirectionalBreadthFirstRouter(warehouseGraph)
    val bidirectionalRouter = BidirectionalBreadthFirstRouter(warehouseGraph)
    val dijkstraRouter = DijkstraRouter(warehouseGraph)

    val unidirectionalPath = unidirectionalRouter.findShortestPath(start, destination) ?: emptyList()
    val bidirectionalPath = bidirectionalRouter.findShortestPath(start, destination) ?: emptyList()
    val dijkstraPath = dijkstraRouter.findShortestPath(start, destination) ?: emptyList()

    printRouteComparison(
        warehouseGraph,
        start,
        destination,
        unidirectionalPath,
        bidirectionalPath,
        dijkstraPath
    )

    BfsBenchmark(warehouseGraph).runAndCompare(start, destination)
}



