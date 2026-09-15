package org.bytebloom.domain.usecase.crud.warehouse

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.WarehouseRepository

class UpdateWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository
) {
    operator fun invoke(warehouse: Warehouse): Warehouse {
        return warehouseRepository.update(warehouse)
    }
}