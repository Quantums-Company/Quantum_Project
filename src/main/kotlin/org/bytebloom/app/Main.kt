package org.bytebloom.app

import org.bytebloom.data.csv.loadPackages
import org.bytebloom.data.csv.loadRoutes
import org.bytebloom.data.csv.loadVehicles
import org.bytebloom.data.csv.loadWarehouses
import org.bytebloom.domain.graph.DomainGraphBuilder
import org.bytebloom.domain.hashing.ConsistentHashingRing
import org.bytebloom.domain.hashing.createValidationReport
import org.bytebloom.domain.printing.printResilienceReport
import org.bytebloom.util.Logger

fun main() {
    //val packages = readPackages("packages.csv").toMutableList()
    // selectionSortPackagesByUrgency(packages)
    // printTopPackages(packages, 3)

    val warehouseRaws = loadWarehouses("warehouses.csv")
    val packageRaws = loadPackages("packages.csv")
    val routeRaws = loadRoutes("routes.csv")
    val vehicleRaws = loadVehicles("fleet.csv")

    val graph = DomainGraphBuilder.buildGraph(
        warehouseRaws,
        packageRaws,
        routeRaws,
        vehicleRaws
    )
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


    val ring = ConsistentHashingRing(
        graph.packages.take(100),
        graph.vehicles.take(4)
    )

    val beforeSnapshot = ring.captureSnapshot()
    val brokenSlot =
        ring.vehicleRing.keys.sorted()[1]

    if(!ring.removeVehicle(brokenSlot)) {
        Logger.warning("the deletion doesn't happen")
        return
    }
    val afterSnapshot = ring.captureSnapshot()

    val report = createValidationReport(
        beforeSnapshot,
        afterSnapshot,
        brokenSlot = brokenSlot
    )
    printResilienceReport(report)
}

