package edu.logiroute.logiroute.ui.sampledata

import org.bytebloom.logiroute.domain.model.Warehouse

object WarehouseSamples {
    val northWarehouse: Warehouse
        get() = Warehouse(
            id = "WH-001",
            name = "Northern Network Node",
            regionalZone = "NORTH",
            longitude = 34.22,
            latitude = 31.45
        )

    val southWarehouse: Warehouse
        get() = Warehouse(
            id = "WH-002",
            name = "Southern Logistics Terminal",
            regionalZone = "SOUTH",
            longitude = 34.45,
            latitude = 31.28
        )

    val eastWarehouse: Warehouse
        get() = Warehouse(
            id = "WH-003",
            name = "Eastern Distribution Hub",
            regionalZone = "EAST",
            longitude = 35.30,
            latitude = 31.70
        )
    val westWarehouse: Warehouse
        get() = Warehouse(
            id = "WH-004",
            name = "Western Coastal Depot",
            regionalZone = "WEST",
            longitude = 34.05,
            latitude = 31.50
        )

    val centralWarehouse: Warehouse
        get() = Warehouse(
            id = "WH-005",
            name = "Central Cargo Terminal - Main Logistics Hub Expansion",
            regionalZone = "CENTRAL",
            longitude = 35.11,
            latitude = 32.04
        ).apply {
            addPackage(PackageSamples.standardCargo)
            addVehicle(VehicleSamples.westVehicle)
            addRoute(RouteSamples.sampleRoute)
        }

    val heavyDataWarehouse: Warehouse
        get() = Warehouse(
            id = "WH-006",
            name = "Mega Distribution Center",
            regionalZone = "CENTRAL",
            longitude = 35.00,
            latitude = 32.00
        ).apply {
            List(50) { index ->
                PackageSamples.standardCargo.copy(
                    id = "PKG-${100 + index}",
                    weight = 5.0 + (index % 10)
                )
            }.forEach { addPackage(it) }

            List(12) { index ->
                VehicleSamples.westVehicle.copy(
                    id = "TRK-${200 + index}",
                    maxCapacityKg = 100.0
                )
            }.forEach { addVehicle(it) }
        }

    val overloadedWarehouse: Warehouse
        get() = Warehouse(
            id = "WH-007",
            name = "Overloaded Regional Terminal",
            regionalZone = "SOUTH",
            longitude = 34.40,
            latitude = 31.30
        ).apply {
            addPackage(PackageSamples.urgentCargo.copy(id = "PKG-991", weight = 500.0))
            addPackage(PackageSamples.standardCargo.copy(id = "PKG-992", weight = 400.0))
            addVehicle(VehicleSamples.westVehicle.copy(id = "TRK-999", maxCapacityKg = 200.0))
        }

    val emptyWarehouse: Warehouse
        get() = Warehouse(
            id = "WH-008",
            name = "Inactive Station",
            regionalZone = "NORTH",
            longitude = 34.20,
            latitude = 31.50
        )
}