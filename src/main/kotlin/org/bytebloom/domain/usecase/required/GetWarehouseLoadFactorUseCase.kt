package org.bytebloom.domain.usecase.required

import org.bytebloom.domain.model.Warehouse


class GetWarehouseLoadFactorUseCase {

    operator fun invoke(warehouse: Warehouse): Double {

        val totalCargoWeight =
            warehouse.cargoQueue.sumOf { it.weight }

        val totalFleetCapacity =
            warehouse.stationedVehicles.sumOf { it.maxCapacityKg }

        if (totalFleetCapacity <= 0.0) {
            return 0.0
        }

        return totalCargoWeight / totalFleetCapacity
    }
}