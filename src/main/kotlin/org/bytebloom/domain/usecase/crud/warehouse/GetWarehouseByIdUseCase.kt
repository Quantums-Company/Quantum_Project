package org.bytebloom.domain.usecase.crud.warehouse

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.domain.validator.ValidationResult
import org.bytebloom.domain.validator.WarehouseIdValidator

class GetWarehouseByIdUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val validator: WarehouseIdValidator
) {
    suspend operator fun invoke(id: String): Warehouse? {

        when (val result = validator(id)) {
            is ValidationResult.Valid -> {
                return warehouseRepository.getById(id)
            }

            is ValidationResult.Invalid -> {
                throw IllegalArgumentException(
                    result.violations.joinToString(", ")
                )
            }
        }
    }
}