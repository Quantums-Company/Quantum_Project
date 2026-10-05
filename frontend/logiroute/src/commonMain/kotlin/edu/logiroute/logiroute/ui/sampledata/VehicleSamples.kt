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
}