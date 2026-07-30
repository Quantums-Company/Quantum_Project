package org.bytebloom.app

import org.bytebloom.domain.model.Package
import org.bytebloom.dataHolder.Priority
import org.bytebloom.logic.DomainGraphBuilder
import org.bytebloom.logic.EcoStrategy
import org.bytebloom.logic.ExpressStrategy
import org.bytebloom.logic.FragileStrategy
import org.bytebloom.logic.RoutePricingEngine
import org.bytebloom.logic.printPackages
import org.bytebloom.logic.printPackagesForFirstWarehouse
import org.bytebloom.logic.printRoutes
import org.bytebloom.logic.printVehicles
import org.bytebloom.logic.printWarehouses
import org.bytebloom.readers.readPackages
import org.bytebloom.readers.readWarehouses
import org.bytebloom.readers.readRoutes
import org.bytebloom.readers.readVehicles

fun main() {
    //val packages = readPackages("packages.csv").toMutableList()
   // selectionSortPackagesByUrgency(packages)
   // printTopPackages(packages, 3)

    val warehouseRaws = readWarehouses("warehouses.csv")
    val packageRaws = readPackages("packages.csv")
    val routeRaws = readRoutes("routes.csv")
    val vehicleRaws = readVehicles("fleet.csv")

    val graph = DomainGraphBuilder.buildGraph(
        warehouseRaws,
        packageRaws,
        routeRaws,
        vehicleRaws
    )

    val firstWarehouse = graph.warehouses.first()
    firstWarehouse.sortCargoByWeight()
    printPackagesForFirstWarehouse(firstWarehouse)

    val samplePackage = Package(
        "PKG-001",
         12.5,
       Priority.STANDARD,
        graph.warehouses[0],
         graph.warehouses[1],
    )
    val pricingEngine = RoutePricingEngine(EcoStrategy())

    println("\n=== Strategy Pattern Validation ===")
    println("Package: ${samplePackage.id}")
    println("Route: ${samplePackage.origin.id} -> ${samplePackage.destination.id}")


    val ecoCost = pricingEngine.calculateShippingCost(
        samplePackage,
        graph.routes
    )
    println("Eco Strategy Cost      : $ecoCost")

    pricingEngine.setStrategy(ExpressStrategy())

    val expressCost = pricingEngine.calculateShippingCost(
        samplePackage,
        graph.routes
    )
    println("Express Strategy Cost : $expressCost")

    pricingEngine.setStrategy(FragileStrategy())

    val fragileCost = pricingEngine.calculateShippingCost(
        samplePackage,
        graph.routes
    )
    println("Fragile Strategy Cost : $fragileCost")
}
