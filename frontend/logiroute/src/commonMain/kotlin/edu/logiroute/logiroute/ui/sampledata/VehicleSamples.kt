package edu.logiroute.logiroute.ui.sampledata

import org.bytebloom.domain.model.Vehicle

object VehicleSamples {
    val westVehicle: Vehicle
        get() = Vehicle(
            id = "TRK-0004",
            maxCapacityKg = 2000.0,
            costPerKm = 2.4,
            currentWarehouse = WarehouseSamples.westWarehouse
        )
    val centralVehicle: Vehicle
        get() = Vehicle(
            id = "TRK-0005",
            maxCapacityKg = 5000.0,
            costPerKm = 4.0,
            currentWarehouse = WarehouseSamples.centralWarehouse
        )

    val safeLoadVehicle: Vehicle
        get() = Vehicle(
            id = "TRK-00060",
            maxCapacityKg = 10000.0,
            costPerKm = 3.5,
            currentWarehouse = WarehouseSamples.centralWarehouse
        )

    val heavyLoadVehicle: Vehicle
        get() = Vehicle(
            id = "TRK-00070",
            maxCapacityKg = 10000.0,
            costPerKm = 4.2,
            currentWarehouse = WarehouseSamples.centralWarehouse
        )

    val overloadedVehicle: Vehicle
        get() = Vehicle(
            id = "TRK-00080",
            maxCapacityKg = 10000.0,
            costPerKm = 5.0,
            currentWarehouse = WarehouseSamples.centralWarehouse
        )
}