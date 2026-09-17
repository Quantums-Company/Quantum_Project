package org.bytebloom.domain.usecase.crud.warehouse

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.domain.validator.CreateWarehouseValidator
import org.bytebloom.domain.validator.ValidationResult

class CreateWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val validator: CreateWarehouseValidator
) {
    suspend operator fun invoke(warehouse: Warehouse): Warehouse {
        when (val result = validator(warehouse)) {
            is ValidationResult.Valid -> {
                return warehouseRepository.create(warehouse)
            }

            is ValidationResult.Invalid -> {
                throw IllegalArgumentException(
                    result.violations.joinToString(", ")
                )
            }
        }
    }
}