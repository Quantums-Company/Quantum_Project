package org.bytebloom.domain.usecase.required

import org.bytebloom.domain.model.Warehouse


class GetWarehouseLoadFactorUseCase {

    operator fun invoke(warehouse: Warehouse): Double =
        warehouse.cargoQueue.sumOf { it.weight } /
                warehouse.stationedVehicles.sumOf { it.maxCapacityKg }


}