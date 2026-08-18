package org.bytebloom.app

import org.bytebloom.data.repository.CsvPackageRepository
import org.bytebloom.data.repository.CsvRouteRepository
import org.bytebloom.data.repository.CsvVehicleRepository
import org.bytebloom.data.repository.CsvWarehouseRepository
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
import org.bytebloom.domain.routing.bfs.UnidirectionalBreadthFirstRouter
import org.bytebloom.domain.routing.dijkstra.DijkstraRouter
import org.bytebloom.util.Logger


fun main() {
    //val packages = readPackages("packages.csv").toMutableList()
    // selectionSortPackagesByUrgency(packages)
    // printTopPackages(packages, 3)

    val warehouseRepo: WarehouseRepository = CsvWarehouseRepository()
    val warehousesById = warehouseRepo.getAllWarehouses().associateBy { it.id }

    val packageRepo: PackageRepository = CsvPackageRepository(warehousesById)
    val routeRepo: RouteRepository = CsvRouteRepository(warehousesById)
    val vehicleRepo: VehicleRepository = CsvVehicleRepository(warehousesById)

    val graph = DomainGraphBuilder.buildGraph(
        warehouseRepo,
        packageRepo,
        routeRepo,
        vehicleRepo
    )


//    val firstWarehouse = graph.warehouses.first()
//    firstWarehouse.sortCargoByWeight()
//    printPackagesForFirstWarehouse(firstWarehouse)
//
//    val samplePackage = Package(
//        "PKG-001",
//        12.5,
//        Priority.STANDARD,
//        graph.warehouses[0],
//        graph.warehouses[1],
//    )
//    val pricingEngine = RoutePricingEngine(EcoStrategy())
//
//    println("\n=== Strategy Pattern Validation ===")
//    println("Package: ${samplePackage.id}")
//    println("Route: ${samplePackage.originWarehouse.id} -> ${samplePackage.destinationWarehouse.id}")
//
//
//    val ecoCost = pricingEngine.calculateShippingCost(
//        samplePackage,
//        graph.routes
//    )
//    println("Eco Strategy Cost      : $ecoCost")
//
//    pricingEngine.setStrategy(ExpressStrategy())
//
//    val expressCost = pricingEngine.calculateShippingCost(
//        samplePackage,
//        graph.routes
//    )
//    println("Express Strategy Cost : $expressCost")
//
//    pricingEngine.setStrategy(FragileStrategy())
//
//    val fragileCost = pricingEngine.calculateShippingCost(
//        samplePackage,
//        graph.routes
//    )
//    println("Fragile Strategy Cost : $fragileCost")
    val packageData = Package(
        "PKG-001",
        12.5,
        Priority.STANDARD,
        graph.warehouses[0],
        graph.warehouses[1],
    )
//    val packageData = graph.packages.first()

    val pricingEngine = RoutePricingEngine(EcoStrategy())

    var service: PackageComponent =
        BasePackageComponent(
            packageData,
            graph.routes,
            pricingEngine
        )

    Logger.info("\n\n==============================================")
    Logger.info("           PACKAGE PRICING REPORT             ")
    Logger.info("==============================================")
    Logger.info("Package ID      : ${service.getPackage().id}")
    Logger.info("Origin          : ${service.getPackage().originWarehouse.id}")
    Logger.info("Destination     : ${service.getPackage().destinationWarehouse.id}")
    Logger.info("Priority        : ${service.getPackage().priority}")
    Logger.info("----------------------------------------------")

    Logger.info("Base Rate       : %.2f".format(service.getTransitRate()))

    service = FragileHandlingDecorator(service, 10.0)

    Logger.info("After Fragile   :  %.2f".format(service.getTransitRate()))

    service = ColdChainDecorator(service, 1.25)

    Logger.info("After Cold Chain: %.2f".format(service.getTransitRate()))

    service = ExpressInsuranceDecorator(service, 20.0)

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

    val graphBuilder = WarehouseGraphBuilder(
        warehouses = graph.warehouses,
        routes = graph.routes
    )

    val warehouseGraph = graphBuilder.build()

    val start = warehousesById["WH-001"]
    val destination = warehousesById["WH-006"]

    println("Start object: ${start}")
    println("Start in graph: ${warehouseGraph.containsWarehouse(start!!)}")
    println("Graph warehouses:")

    warehouseGraph.warehouses().forEach {
        println("${it.id} -> ${it === start}")
    }

    val bfsRouter = UnidirectionalBreadthFirstRouter(warehouseGraph)
    val dijkstraRouter = DijkstraRouter(warehouseGraph)

    println("Start object: ${start}")
    println("Start in graph: ${warehouseGraph.containsWarehouse(start!!)}")
    println("Graph warehouses:")

    warehouseGraph.warehouses().forEach {
        println("${it.id} -> ${it === start}")
    }

    if (start == null || destination == null) {
        Logger.warning("there is no warehouse with id = [WH-001] or = [WH-006]")
        return
    }
    val bfsPath = bfsRouter.findShortestPath(start, destination) ?: emptyList()
    val dijkstraPath = dijkstraRouter.findShortestPath(start, destination) ?: emptyList()


    printRouteComparison(warehouseGraph, start, destination, bfsPath, dijkstraPath)
}





