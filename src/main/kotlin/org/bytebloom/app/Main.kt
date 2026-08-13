package org.bytebloom.app

//import org.bytebloom.data.csv.loadPackages
//import org.bytebloom.data.csv.loadRoutes
//import org.bytebloom.data.csv.loadVehicles
//import org.bytebloom.data.csv.loadWarehouses
//import org.bytebloom.domain.printing.printResilienceReport
import org.bytebloom.domain.graph.DomainGraphBuilder
import org.bytebloom.domain.hashing.ConsistentHashingRing
import org.bytebloom.domain.hashing.createValidationReport
import org.bytebloom.util.Logger
import org.bytebloom.data.repository.*
import org.bytebloom.domain.printing.printResilienceReport
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.repository.VehicleRepository
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.domain.pricing.PackageComponent
import org.bytebloom.domain.pricing.BasePackageComponent
import org.bytebloom.domain.pricing.FragileHandlingDecorator
import org.bytebloom.domain.pricing.ColdChainDecorator
import org.bytebloom.domain.pricing.ExpressInsuranceDecorator
import org.bytebloom.domain.pricing.RoutePricingEngine
import org.bytebloom.domain.pricing.EcoStrategy

//fun main() {
//    //val packages = readPackages("packages.csv").toMutableList()
//    // selectionSortPackagesByUrgency(packages)
//    // printTopPackages(packages, 3)
//
//    val warehouseRaws = loadWarehouses("warehouses.csv")
//    val packageRaws = loadPackages("packages.csv")
//    val routeRaws = loadRoutes("routes.csv")
//    val vehicleRaws = loadVehicles("fleet.csv")
//
//    val graph = DomainGraphBuilder.buildGraph(
//        warehouseRaws,
//        packageRaws,
//        routeRaws,
//        vehicleRaws
//    )


//    val firstWarehouse = graph.warehouses.first()
//    firstWarehouse.sortCargoByWeight()
//    printPackagesForFirstWarehouse(firstWarehouse)
//
//    val samplePackage = Package(
//        "PKG-001",
//         12.5,
//       Priority.STANDARD,
//        graph.warehouses[0],
//         graph.warehouses[1],
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

fun main() {
    val warehouseRepo: WarehouseRepository = CsvWarehouseRepository()
    val packageRepo: PackageRepository = CsvPackageRepository()
    val routeRepo: RouteRepository = CsvRouteRepository()
    val vehicleRepo: VehicleRepository = CsvVehicleRepository()

    val warehouseRaws = warehouseRepo.getAllWarehouses()
    val packageRaws = packageRepo.getAllPackages()
    val routeRaws = routeRepo.getAllRoutes()
    val vehicleRaws = vehicleRepo.getAllVehicles()

    val graph = DomainGraphBuilder.buildGraph(
        warehouseRaws,
        packageRaws,
        routeRaws,
        vehicleRaws
    )


    val packageData = graph.packages.first()

    val pricingEngine = RoutePricingEngine(EcoStrategy())

    var service: PackageComponent =
        BasePackageComponent(
            packageData,
            graph.routes,
            pricingEngine
        )

    service = FragileHandlingDecorator(service, 10.0)
    service = ColdChainDecorator(service, 1.25)
    service = ExpressInsuranceDecorator(service, 20.0)

    println("Package: ${service.getPackage().id}")
    println("Final transit rate: ${service.getTransitRate()}")



}





