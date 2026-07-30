package org.bytebloom.app

import org.bytebloom.logic.DomainGraphBuilder
import org.bytebloom.readers.readPackages
import org.bytebloom.readers.readWarehouses
import org.bytebloom.readers.readRoutes
import org.bytebloom.readers.readVehicles

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.logic.EcoStrategy
import org.bytebloom.logic.ExpressStrategy
import org.bytebloom.logic.RoutePricingEngine

import org.bytebloom.dataHolder.packageRaw
import org.bytebloom.dataHolder.routeRaw
import org.bytebloom.dataHolder.warehouseRaw
import org.bytebloom.logic.printTopPackages
import org.bytebloom.logic.quickSortCargoByWeight
import org.bytebloom.readers.readPackages
import org.bytebloom.logic.selectionSortPackagesByUrgency

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

    val firstWarehouse = graph.warehouses[0]
    firstWarehouse.sortCargoByWeight()
    firstWarehouse.cargoQueue.forEach {
        println("${it.id} - ${it.weight}")
    }

    val warehouseA = Warehouse("W1", "Main Warehouse", "Zone A", 0.0, 0.0)
    val warehouseB = Warehouse("W2", "Secondary Warehouse", "Zone B", 0.0, 0.0)

    val samplePackage = Package(
        id = "PKG-001",
        weight = 12.5,
        priority = org.bytebloom.dataHolder.Priority.STANDARD,
        origin = warehouseA,
        destination = warehouseB
    )

    val routesList = listOf(
        Route(
            routeId = "R1",
            origin = warehouseA,
            destination = warehouseB,
            distanceKm = 45.0,
            typicalDelayMin = 10
        )
    )
    val pricingEngine = RoutePricingEngine(EcoStrategy())
    val ecoCost = pricingEngine.calculatePackageCost(samplePackage, routesList)
    println("Eco Shipping Cost: $ecoCost")

    pricingEngine.setStrategy(ExpressStrategy())
    val expressCost = pricingEngine.calculatePackageCost(samplePackage, routesList)
    println("Express Shipping Cost: $expressCost")
}
