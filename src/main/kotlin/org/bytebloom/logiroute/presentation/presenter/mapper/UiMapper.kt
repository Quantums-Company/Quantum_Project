package org.bytebloom.logiroute.presentation.presenter.mapper

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Priority
import org.bytebloom.logiroute.domain.model.Region
import org.bytebloom.logiroute.domain.model.Route
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.VehicleTelemetry
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.presentation.model.PackageUi
import org.bytebloom.logiroute.presentation.model.PriorityUi
import org.bytebloom.logiroute.presentation.model.RouteUi
import org.bytebloom.logiroute.presentation.model.RegionUi
import org.bytebloom.logiroute.presentation.model.VehicleTelemetryUi
import org.bytebloom.logiroute.presentation.model.VehicleUi
import org.bytebloom.logiroute.presentation.model.WarehouseUi

internal fun Warehouse.toUi() = WarehouseUi(
    id = id,
    name = name,
    zone = regionalZone,
    latitude = latitude,
    longitude = longitude
)

internal fun Vehicle.toUi() = VehicleUi(
    id = id,
    capacityKg = maxCapacityKg,
    costPerKm = costPerKm,
    warehouseName = currentWarehouse.name
)

internal fun Package.toUi() = PackageUi(
    id = id,
    weightKg = weight,
    priority = priority.toUi(),
    originName = originWarehouse.name,
    destinationName = destinationWarehouse.name
)

internal fun Route.toUi() = RouteUi(
    id = id,
    originName = originWarehouse.name,
    destinationName = destinationWarehouse.name,
    distanceKm = distanceKm,
    typicalDelayMin = typicalDelayMin
)

internal fun VehicleTelemetry.toUi() = VehicleTelemetryUi(
    vehicleId = vehicleId,
    latitude = latitude,
    longitude = longitude,
    speedKph = speedKph,
    fuelPercent = fuelPercent
)

internal fun Priority.toUi(): PriorityUi = when (this) {
    Priority.URGENT -> PriorityUi.URGENT
    Priority.STANDARD -> PriorityUi.STANDARD
    Priority.LOW -> PriorityUi.LOW
}

internal fun Region.toUi(): RegionUi = when (this) {
    Region.WEST -> RegionUi.WEST
    Region.EAST -> RegionUi.EAST
    Region.NORTH -> RegionUi.NORTH
    Region.SOUTH -> RegionUi.SOUTH
    Region.CENTRAL -> RegionUi.CENTRAL
}
