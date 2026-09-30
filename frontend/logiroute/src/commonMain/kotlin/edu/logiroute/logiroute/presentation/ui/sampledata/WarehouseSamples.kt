package edu.logiroute.logiroute.presentation.ui.sampledata

import org.bytebloom.domain.model.Warehouse

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
}