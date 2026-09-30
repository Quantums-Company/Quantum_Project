package org.bytebloom.domain.usecase.crud.warehouse

import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.validator.id.WarehouseIdValidator

class DeleteWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val validator: WarehouseIdValidator
) {
    suspend operator fun invoke(id: String): Boolean =
        when (val result = validator(id)) {
            is ValidatorResult.Valid -> warehouseRepository.delete(id)
            is ValidatorResult.Invalid -> throw EntityValidationException(result.violations)
        }
}