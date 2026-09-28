package org.bytebloom.domain.usecase.crud.warehouse

import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.domain.validator.ValidatorResult
import org.bytebloom.domain.validator.id.WarehouseIdValidator

class GetWarehouseByIdUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val validator: WarehouseIdValidator
) {
    suspend operator fun invoke(id: String): Warehouse? =
        when (val result = validator(id)) {
            is ValidatorResult.Valid -> warehouseRepository.getById(id)
            is ValidatorResult.Invalid -> throw EntityValidationException(result.violations)
        }
}