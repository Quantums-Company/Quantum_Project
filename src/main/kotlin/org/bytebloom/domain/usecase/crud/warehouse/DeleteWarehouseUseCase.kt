package org.bytebloom.domain.usecase.crud.warehouse

import org.bytebloom.domain.repository.WarehouseRepository

class DeleteWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository
) {
    operator fun invoke(id: String): Boolean {
        return warehouseRepository.delete(id)
    }
}