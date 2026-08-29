package org.bytebloom.presentation

import org.bytebloom.data.repository.CsvPackageRepository
import org.bytebloom.data.repository.CsvRouteRepository
import org.bytebloom.data.repository.CsvVehicleRepository
import org.bytebloom.data.repository.CsvWarehouseRepository
import org.bytebloom.domain.graph.DomainGraph
import org.bytebloom.domain.graph.DomainGraphBuilder
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.performance.AVLTree
import org.bytebloom.domain.performance.BST
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
import org.bytebloom.domain.performance.PackageTrackingIdGenerator
import org.bytebloom.domain.performance.TreePerformanceReport

private const val DEMO_ROUTE_ORIGIN_ID = "WH-031"
private const val DEMO_ROUTE_DESTINATION_ID = "WH-091"

private const val FRAGILE_HANDLING_SURCHARGE = 10.0
private const val COLD_CHAIN_MULTIPLIER = 1.25
private const val EXPRESS_INSURANCE_SURCHARGE = 20.0

class AnalyzeTreePerformanceUseCase(
    private val trackingIdGenerator: PackageTrackingIdGenerator,
    private val binarySearchTree: BST,
    private val avlTree: AVLTree
) {

    operator fun invoke(
        packageCount: Int,
        targetTrackingId: String
    ): TreePerformanceReport {

        val trackingIds =
            trackingIdGenerator.generate(packageCount)

        trackingIds.forEach { trackingId ->
            binarySearchTree.insert(trackingId)
            avlTree.insert(trackingId)
        }

        return TreePerformanceReport(
            totalPackages = packageCount,
            trackingId = targetTrackingId,
            binarySearchTreeSteps =
                binarySearchTree.search(targetTrackingId),
            avlTreeSteps =
                avlTree.search(targetTrackingId)
        )
    }
}

fun main() {
//    val warehouseRepo: WarehouseRepository = CsvWarehouseRepository()
//    val warehousesById = warehouseRepo.getAll().associateBy { it.id }
//
//    val packageRepo: PackageRepository = CsvPackageRepository(warehousesById)
//    val routeRepo: RouteRepository = CsvRouteRepository(warehousesById)
//    val vehicleRepo: VehicleRepository = CsvVehicleRepository(warehousesById)
//
//    val graph = DomainGraphBuilder(
//        warehouseRepo,
//        packageRepo,
//        routeRepo,
//        vehicleRepo
//    ).buildGraph()
//
//    runPricingDemo(graph,routeRepo)
//    runRoutingDemo(graph)


    val trackingIdGenerator =
        PackageTrackingIdGenerator()

    val binarySearchTree =
        BST()

    val avlTree =
        AVLTree()

    val analyzeTreePerformance =
        AnalyzeTreePerformanceUseCase(
            trackingIdGenerator = trackingIdGenerator,
            binarySearchTree = binarySearchTree,
            avlTree = avlTree
        )

    val report =
        analyzeTreePerformance(
            packageCount = 1000,
            targetTrackingId = "PKG-001000"
        )

    println()
    println("==============================================")
    println("          TREE PERFORMANCE ANALYSIS")
    println("==============================================")
    println("Packages                  : ${report.totalPackages}")
    println("Target Tracking ID        : ${report.trackingId}")
    println(
        "Unbalanced BST steps      : " +
                report.binarySearchTreeSteps
    )
    println(
        "AVL Tree steps            : " +
                report.avlTreeSteps
    )
    println("==============================================")
}

private fun runPricingDemo(graph: DomainGraph, routeRepository: RouteRepository) {
    val packageData = Package(
        "PKG-001",
        12.5,
        Priority.STANDARD,
        graph.warehouses[0],
        graph.warehouses[1],
    )

    val pricingEngine = RoutePricingEngine(EcoStrategy(), routeRepository)

    var service: PackageComponent = BasePackageComponent(pricingEngine)

    Logger.info("\n==============================================")
    Logger.info("           PACKAGE PRICING REPORT             ")
    Logger.info("==============================================")
    Logger.info("Package ID      : ${packageData.id}")
    Logger.info("Origin          : ${packageData.originWarehouse.id}")
    Logger.info("Destination     : ${packageData.destinationWarehouse.id}")
    Logger.info("Priority        : ${packageData.priority}")
    Logger.info("----------------------------------------------")

    Logger.info("Base Rate       : %.2f".format(service.getTransitRate(packageData)))

    service = FragileHandlingDecorator(service, FRAGILE_HANDLING_SURCHARGE)
    Logger.info("After Fragile   : %.2f".format(service.getTransitRate(packageData)))

    service = ColdChainDecorator(service, COLD_CHAIN_MULTIPLIER)
    Logger.info("After Cold Chain: %.2f".format(service.getTransitRate(packageData)))

    service = ExpressInsuranceDecorator(service, EXPRESS_INSURANCE_SURCHARGE)
    val finalRate = service.getTransitRate(packageData)

    if (finalRate == null) {
        Logger.warning(
            "No direct route found for package ${packageData.id}. " +
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



