package edu.logiroute.logiroute.presentation.ui.sampledata

import org.bytebloom.domain.model.Vehicle

object VehicleSamples {
    val westVehicle: Vehicle
        get() = Vehicle("TRK-0004", 2000.0, 2.4, WarehouseSamples.westWarehouse)
}