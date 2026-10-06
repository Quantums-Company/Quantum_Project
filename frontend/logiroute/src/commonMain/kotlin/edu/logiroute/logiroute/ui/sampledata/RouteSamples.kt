package edu.logiroute.logiroute.ui.sampledata

import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Warehouse

object RouteSamples {
    val sampleRoute = Route(
        id = "RT-501",
        distanceKm = 145.0,
        typicalDelayMin = 75,
        originWarehouse = WarehouseSamples.northWarehouse,
        destinationWarehouse = WarehouseSamples.eastWarehouse
    )

    val shortLocalRoute: Route
        get() = Route(
            id = "RT-000001",
            distanceKm = 35.0,
            typicalDelayMin = 25,
            originWarehouse = WarehouseSamples.northWarehouse,
            destinationWarehouse = WarehouseSamples.eastWarehouse
        )

    val longCrossCountryRoute: Route
        get() {
            val longOriginWarehouse = Warehouse(
                id = "WH-000091",
                name = "Continental Megahub Logistics Center Expansion Alpha",
                regionalZone = WarehouseSamples.northWarehouse.regionalZone,
                longitude = WarehouseSamples.northWarehouse.longitude,
                latitude = WarehouseSamples.northWarehouse.latitude,
            )

            val longDestinationWarehouse = Warehouse(
                id = "WH-000092",
                name = "Northern Transshipment Maritime Terminal Node Omega",
                regionalZone = WarehouseSamples.eastWarehouse.regionalZone,
                longitude = WarehouseSamples.eastWarehouse.longitude,
                latitude = WarehouseSamples.eastWarehouse.latitude,
            )

            return Route(
                id = "RT-000099",
                distanceKm = 850.0,
                typicalDelayMin = 145,
                originWarehouse = longOriginWarehouse,
                destinationWarehouse = longDestinationWarehouse
            )
        }


}