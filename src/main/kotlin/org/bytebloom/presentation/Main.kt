package org.bytebloom.presentation

import org.bytebloom.di.networkModule
import org.bytebloom.di.repositoryModule
import org.bytebloom.di.useCaseModule
import org.bytebloom.di.validatorModule
import org.koin.core.context.startKoin
import org.bytebloom.domain.knapsack.KnapsackCargoOptimizer
import org.bytebloom.domain.model.CargoItem
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.dispatch.DispatchOrder
import org.bytebloom.domain.dispatch.StandardDispatchProcessor
import org.bytebloom.domain.shipment.ShipmentTracker

fun main()  {
    val koin = startKoin {
        modules(
            networkModule,
            repositoryModule,
            validatorModule,
            useCaseModule
        )
    }.koin

    println("=== Sub-Task 5: End-to-End Verification ===")

    val optimizer = koin.get<KnapsackCargoOptimizer>()

    val cargoItems = listOf(
        CargoItem("PKG-001", 10, 3),
        CargoItem("PKG-002", 20, 2),
        CargoItem("PKG-003", 15, 3),
        CargoItem("PKG-004", 5, 1)
    )

    val result = optimizer(
        items = cargoItems,
        capacityKg = 30
    )

    println("Selected cargo: ${result.selectedItems}")
    println("Total weight: ${result.totalWeightKg} kg")
    println("Total priority: ${result.totalPriorityValue}")


    val warehouse = Warehouse(
        id = "WH-001",
        name = "Main Warehouse",
        regionalZone = "North",
        longitude = 35.95,
        latitude = 32.22
    )

    val destination = Warehouse(
        id = "WH-002",
        name = "Destination Warehouse",
        regionalZone = "South",
        longitude = 35.93,
        latitude = 32.20
    )

    val packages = listOf(
        Package("PKG-001", 10.0, Priority.URGENT, warehouse, destination),
        Package("PKG-002", 20.0, Priority.STANDARD, warehouse, destination),
        Package("PKG-003", 15.0, Priority.URGENT, warehouse, destination),
        Package("PKG-004", 5.0, Priority.LOW, warehouse, destination)
    )

    packages.forEach { warehouse.addPackage(it) }

    val vehicle = Vehicle(
        id = "TRK-001",
        maxCapacityKg = 30.0,
        costPerKm = 2.0,
        currentWarehouse = warehouse
    )

    warehouse.addVehicle(vehicle)

    val selectedPackages = packages.filter { pkg ->
        result.selectedItems.any { item -> item.id == pkg.id }
    }

    println("\nSelected packages for dispatch:")
    selectedPackages.forEach { println("${it.id} - ${it.weight}kg - ${it.priority}") }

    val shipmentTracker = ShipmentTracker()

    val dispatcher = StandardDispatchProcessor(shipmentTracker)

    val order = DispatchOrder(
        packages = selectedPackages,
        vehicle = vehicle,
        warehouse = warehouse
    )

    val outcome = dispatcher.dispatch(order)

    println("\nDispatch outcome: $outcome")

    println("\nShipment states:")

    selectedPackages.forEach { pkg ->
        println("${pkg.id}: ${shipmentTracker.historyOf(pkg.id).map { it.name }}")
    }


}



