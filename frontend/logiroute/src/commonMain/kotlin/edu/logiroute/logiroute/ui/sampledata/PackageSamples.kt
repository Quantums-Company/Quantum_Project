package edu.logiroute.logiroute.ui.sampledata

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority

object PackageSamples {
    val urgentCargo: Package
        get() = Package(
            id = "PKG-001",
            weight = 12.5,
            priority = Priority.URGENT,
            originWarehouse = WarehouseSamples.westWarehouse,
            destinationWarehouse = WarehouseSamples.centralWarehouse
        )

    val standardCargo: Package
        get() = Package(
            id = "PKG-092",
            weight = 5.2,
            priority = Priority.STANDARD,
            originWarehouse = WarehouseSamples.southWarehouse,
            destinationWarehouse = WarehouseSamples.westWarehouse
        )

    val lowCargo: Package
        get() = Package(
            id = "PKG-741",
            weight = 150.0,
            priority = Priority.LOW,
            originWarehouse = WarehouseSamples.northWarehouse,
            destinationWarehouse = WarehouseSamples.centralWarehouse
        )
}