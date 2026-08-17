package org.bytebloom.data.mapper

import org.bytebloom.data.raw.VehicleRaw
import org.bytebloom.domain.model.Vehicle
import org.bytebloom.domain.model.Warehouse

fun VehicleRaw.toDomain(
    currentWarehouse: Warehouse
): Vehicle {
    return Vehicle(
        id = id,
        maxCapacityKg = maxCapacityKg,
        costPerKm = costPerKm,
        currentWarehouse = currentWarehouse
    )
}