package edu.logiroute.logiroute.ui.sampledata

import org.bytebloom.domain.model.Route

object RouteSamples {
    val sampleRoute = Route(
        id = "RT-501",
        distanceKm = 145.0,
        typicalDelayMin = 75,
        originWarehouse = WarehouseSamples.northWarehouse,
        destinationWarehouse = WarehouseSamples.eastWarehouse
    )
}