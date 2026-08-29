package org.bytebloom.presentation

import org.bytebloom.data.repository.CsvPackageRepository
import org.bytebloom.data.repository.CsvRouteRepository
import org.bytebloom.data.repository.CsvVehicleRepository
import org.bytebloom.data.repository.CsvWarehouseRepository
import org.bytebloom.domain.model.DomainGraph
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.tree.binary.AVLTree
import org.bytebloom.domain.tree.binary.BST
import org.bytebloom.domain.performance.PackageTrackingIdGenerator
import org.bytebloom.domain.performance.TreePerformanceReport
import org.bytebloom.domain.performance.TreeSearchResult
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
import org.bytebloom.domain.usecase.FindPackagesAboveWeightUseCase
import org.bytebloom.domain.usecase.GetNetworkStatisticsUseCase

private const val DEMO_ROUTE_ORIGIN_ID = "WH-031"
private const val DEMO_ROUTE_DESTINATION_ID = "WH-091"

private const val FRAGILE_HANDLING_SURCHARGE = 10.0
private const val COLD_CHAIN_MULTIPLIER = 1.25
private const val EXPRESS_INSURANCE_SURCHARGE = 20.0

class AnalyzeTreePerformanceUseCase(
    private val trackingIdGenerator: PackageTrackingIdGenerator,
    private val binarySearchTree: BST<String>,
    private val avlTree: AVLTree<String>
) {

    operator fun invoke(
        packageCount: Int,
        targetTrackingIds: List<String>
    ): TreePerformanceReport {

        val trackingIds =
            trackingIdGenerator.generate(packageCount)

        trackingIds.forEach {
            binarySearchTree.insert(it)
            avlTree.insert(it)
        }

        val results =
            targetTrackingIds.map { trackingId ->
                TreeSearchResult(
                    trackingId = trackingId,
                    binarySearchTreeSteps =
                        binarySearchTree.search(trackingId),
                    avlTreeSteps =
                        avlTree.search(trackingId)
                )
            }

        return TreePerformanceReport(
            totalPackages = packageCount,
            results = results
        )
    }
}

fun main() {
    val warehouseRepo: WarehouseRepository = CsvWarehouseRepository()
    val warehousesById = warehouseRepo.getAll().associateBy { it.id }

    val packageRepo: PackageRepository = CsvPackageRepository(warehousesById)
    val routeRepo: RouteRepository = CsvRouteRepository(warehousesById)
    val vehicleRepo: VehicleRepository = CsvVehicleRepository(warehousesById)

    val graph = DomainGraph(
        warehouseRepo.getAll(),
        packageRepo.getAll(),
        routeRepo.getAll(),
        vehicleRepo.getAll(),
    )

    runPricingDemo(graph,routeRepo)
    runRoutingDemo(graph)

    val useCase =
        AnalyzeTreePerformanceUseCase(
            PackageTrackingIdGenerator(),
            BST<String>(),
            AVLTree<String>()
        )
    val report =
        useCase.invoke(
            packageCount = 1000,
            listOf(
                "PKG-000001",
                "PKG-000500",
                "PKG-001000"
            )
        )

    println()
    println("========================================")
    println("       TREE PERFORMANCE ANALYSIS")
    println("========================================")
    println()
    println("Packages: ${report.totalPackages}")

    report.results.forEach { result ->
        println()
        println("Target: ${result.trackingId}")
        println("BST steps: ${result.binarySearchTreeSteps}")
        println("AVL steps: ${result.avlTreeSteps}")
    }

    println()
    println("========================================")

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


private fun runPackageWeightDemo(packageRepository: PackageRepository) {
    val findPackagesAboveWeight = FindPackagesAboveWeightUseCase(packageRepository)

    val heavyPackages = findPackagesAboveWeight(20.0)

    Logger.info("\n==============================================")
    Logger.info("       PACKAGES ABOVE WEIGHT REPORT          ")
    Logger.info("==============================================")
    Logger.info("Minimum Weight : 20.0 kg")
    Logger.info("Matching Packages:")

    heavyPackages.forEach {
        Logger.info("${it.id} : ${it.weight} kg")
    }

    Logger.info("Total Found : ${heavyPackages.size}")
    Logger.info("==============================================")
}

private fun runNetworkStatisticsDemo(
    warehouseRepository: WarehouseRepository,
    packageRepository: PackageRepository,
    vehicleRepository: VehicleRepository
) {
    val getNetworkStatistics = GetNetworkStatisticsUseCase(
        warehouseRepository,
        packageRepository,
        vehicleRepository
    )

    val statistics = getNetworkStatistics()

    Logger.info("\n==============================================")
    Logger.info("           NETWORK STATISTICS REPORT          ")
    Logger.info("==============================================")
    Logger.info("Warehouses           : ${statistics.warehouseCount}")
    Logger.info("Packages             : ${statistics.packageCount}")
    Logger.info("Vehicles             : ${statistics.vehicleCount}")
    Logger.info("Total Package Weight : ${statistics.totalPackageWeight} kg")
    Logger.info("Total Vehicle Capacity: ${statistics.totalVehicleCapacity} kg")
    Logger.info("==============================================")
}