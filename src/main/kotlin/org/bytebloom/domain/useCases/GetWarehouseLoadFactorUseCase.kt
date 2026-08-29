package org.bytebloom.domain.useCases

import org.bytebloom.domain.model.Warehouse


class GetWarehouseLoadFactorUseCase {

    operator fun invoke(warehouse: Warehouse): Double =
        warehouse.cargoQueue.map { it.weight }.sum() / warehouse.stationedVehicles.map { it.maxCapacityKg }.sum()
}