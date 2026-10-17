package org.bytebloom.logiroute.domain.usecase.crud.warehouses

import org.bytebloom.logiroute.domain.model.Warehouse


class GetWarehouseLoadFactorUseCase {

    operator fun invoke(warehouse: Warehouse): Double {

        val cargoWeight =
            warehouse.cargoQueue.sumOf { it.weight }

        val fleetCapacity =
            warehouse.stationedVehicles.sumOf { it.maxCapacityKg }

        return if (fleetCapacity > 0.0) {
            cargoWeight / fleetCapacity
        } else {
            0.0
        }
    }
}