package edu.logiroute.logiroute.presentation.ui.sampledata

import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse

object VehicleSamples {
    val northVehicle: Vehicle
        get() = Vehicle("TRK-01", 1500.0, 1.8, WarehouseSamples.northWarehouse)

    val southVehicle: Vehicle
        get() = Vehicle("TRK-02", 3000.0, 3.2, WarehouseSamples.southWarehouse)

    val eastVehicle: Vehicle
        get() = Vehicle("TRK-0003", 800.0, 1.5, WarehouseSamples.eastWarehouse)

    val westVehicle: Vehicle
        get() = Vehicle("TRK-0004", 2000.0, 2.4, WarehouseSamples.westWarehouse)

    val centralVehicle: Vehicle
        get() = Vehicle("TRK-0005", 5000.0, 4.0, WarehouseSamples.centralWarehouse)
}