package org.bytebloom.app

import org.bytebloom.logic.DomainGraphBuilder
import org.bytebloom.readers.readPackages
import org.bytebloom.readers.readWarehouses
import org.bytebloom.readers.readRoutes
import org.bytebloom.readers.readVehicles

import org.bytebloom.dataHolder.packageRaw
import org.bytebloom.dataHolder.routeRaw
import org.bytebloom.dataHolder.warehouseRaw
import org.bytebloom.logic.printTopPackages
import org.bytebloom.logic.quickSortCargo
import org.bytebloom.readers.readPackages
import org.bytebloom.logic.selectionSortPackagesByUrgency

fun main() {
    //val packages = readPackages("packages.csv").toMutableList()

   // selectionSortPackagesByUrgency(packages)

   // printTopPackages(packages, 3)

    val warehouseRaws = readWarehouses("warehouse.csv")
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
    firstWarehouse.sortCargo()
    firstWarehouse.cargoQueue.forEach {
        println("${it.id} - ${it.weight}")
    }


}