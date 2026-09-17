package org.bytebloom.domain.usecase.crud.warehouse

import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.domain.validator.ValidationResult
import org.bytebloom.domain.validator.WarehouseIdValidator

class DeleteWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val validator: WarehouseIdValidator
) {
    suspend operator fun invoke(id: String): Boolean {

        when (val result = validator(id)) {
            is ValidationResult.Valid -> {
                return warehouseRepository.delete(id)
            }

            is ValidationResult.Invalid -> {
                throw IllegalArgumentException(
                    result.violations.joinToString(", ")
                )
            }
        }
    }
}