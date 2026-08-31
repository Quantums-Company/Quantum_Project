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
import org.bytebloom.domain.tree.hierarchicalHub.HubTree
import org.bytebloom.domain.tree.hierarchicalHub.HubTreeBuilder
import org.bytebloom.domain.tree.hierarchicalHub.HubTreeNode
import org.bytebloom.domain.tree.hierarchicalHub.HubType
import org.bytebloom.domain.usecase.BestVehicleByCostCapacityUseCase
import org.bytebloom.domain.usecase.EstimateShipmentDeliveryUseCase
import org.bytebloom.util.Logger
import org.bytebloom.domain.usecase.FindPackagesAboveWeightUseCase
import org.bytebloom.domain.usecase.GetWarehouseReportUseCase
import org.bytebloom.domain.usecase.TraceHubLineageUseCase
import org.bytebloom.domain.usecase.required.FindOptimalPathUseCase
import org.bytebloom.domain.usecase.FindAllPairsShortestPathUseCase
import org.bytebloom.domain.usecase.required.GetWarehouseLoadFactorUseCase
import org.bytebloom.domain.usecase.BestVehicleByCostCapacityUseCase

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


    val warehouses = warehouseRepo.getAll()
    val selectionWarehouses = warehouses.take(5)
    val findAllPairsShortestPathUseCase = FindAllPairsShortestPathUseCase(
        routeRepo
    )
    val allPairsShortestPaths = findAllPairsShortestPathUseCase(warehouses)

    Logger.info("\n================ALL PAIRS SHORTEST PATHS==================")


    Logger.info("origin".padEnd(12) +
            selectionWarehouses.joinToString("") { it.id.padStart(12) }
    )

    selectionWarehouses.forEach { origin ->
        val row = buildString {
            append(origin.id.padEnd(12))
            selectionWarehouses.forEach { destination ->
                val distance = allPairsShortestPaths[origin]?.get(destination)

                append(
                    String.format("%12.2f", distance)
                )
            }
        }
        Logger.info(row)
    }
    Logger.info("=========================================================")

    val packages = listOf(
        packageRepo.getAll()[0], packageRepo.getAll()[1], packageRepo.getAll()[2]
    )

    val bestVehicleUseCase = BestVehicleByCostCapacityUseCase(vehicleRepo)
    val bestVehicles = bestVehicleUseCase(packages)

    Logger.info("Best vehicles : ")

    bestVehicles.forEach {
        Logger.info(
            "Vehicle: ${it.id}," +
                " Capacity: ${it.maxCapacityKg}," +
                " Cost: ${it.costPerKm}"
        )
    }

//    runPricingDemo(graph,routeRepo)
//    runRoutingDemo(graph)
    val findOptimalPathUseCase = FindOptimalPathUseCase(
        warehouseRepository = warehouseRepo,
        routeRepository = routeRepo
    )
    val estimateShipmentDeliveryUseCase =
        EstimateShipmentDeliveryUseCase(
            packageRepository = packageRepo,
            routeRepository = routeRepo,
            findOptimalPath = findOptimalPathUseCase
        )

    val estimatedTime =
        estimateShipmentDeliveryUseCase("PKG-000005")

    Logger.info(
        "Estimated Delivery Time: ${
            estimatedTime?.let { "$it minutes" }
                ?: "Unable to estimate"
        }"
    )

    val loadFactorUseCase =
        GetWarehouseLoadFactorUseCase()

    val hubTreeBuilder =
        HubTreeBuilder(loadFactorUseCase)

    val hubTree =
        hubTreeBuilder.build(graph.warehouses)

    val traceHubLineageUseCase =
        TraceHubLineageUseCase(hubTree)

    val warehouse =
        graph.warehouses.first()


    val lineage = traceHubLineageUseCase(warehouse)

    println("Hub Lineage")
    println("-----------")

    lineage.forEachIndexed { index, hub ->
        val type = hubTree.findNode(hub)?.type

        if (index > 0) {
            println("    ↓")
        }

        println("${hub.id} [$type]")
    }

//    val useCase =
//        AnalyzeTreePerformanceUseCase(
//            PackageTrackingIdGenerator(),
//            BST<String>(),
//            AVLTree<String>()
//        )
//    val report =
//        useCase.invoke(
//            packageCount = 1000,
//            listOf(
//                "PKG-000001",
//                "PKG-000500",
//                "PKG-001000"
//            )
//        )
//
//    println()
//    println("========================================")
//    println("       TREE PERFORMANCE ANALYSIS")
//    println("========================================")
//    println()
//    println("Packages: ${report.totalPackages}")
//
//    report.results.forEach { result ->
//        println()
//        println("Target: ${result.trackingId}")
//        println("BST steps: ${result.binarySearchTreeSteps}")
//        println("AVL steps: ${result.avlTreeSteps}")
//    }
//
//    println()
//    println("========================================")

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
private fun runWarehouseReportDemo(
    warehouseRepository: WarehouseRepository,
    packageRepository: PackageRepository,
    vehicleRepository: VehicleRepository
) {
    val getWarehouseReport = GetWarehouseReportUseCase(
        warehouseRepository,
        packageRepository,
        vehicleRepository
    )

    val report = getWarehouseReport("WH-001")

    if (report == null) {
        Logger.warning("Warehouse 'WH-001' was not found.")
        return
    }

    Logger.info("\n==============================================")
    Logger.info("            WAREHOUSE REPORT")
    Logger.info("==============================================")
    Logger.info("Warehouse ID           : ${report.warehouseId}")
    Logger.info("Package Count          : ${report.packageCount}")
    Logger.info("Total Package Weight   : ${report.totalPackageWeight} kg")
    Logger.info("Total Vehicle Capacity : ${report.totalVehicleCapacity} kg")
    Logger.info("==============================================")
}

