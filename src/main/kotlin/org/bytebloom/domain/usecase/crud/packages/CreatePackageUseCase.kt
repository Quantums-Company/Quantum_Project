package org.bytebloom.domain.usecase.crud.packages

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.validator.create.CreatePackageValidator
import org.bytebloom.domain.validation.ValidationResult
import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.service.IdGenerator
import org.bytebloom.domain.validation.EntityType


class CreatePackageUseCase(
    private val packageRepository: PackageRepository,
    private val validator: CreatePackageValidator,
    private val idGenerator: IdGenerator
) {
    suspend operator fun invoke( weight: Double, priority: Priority, originWarehouse: Warehouse, destinationWarehouse: Warehouse): Package {

        val pkg = Package(
            id = idGenerator.next(EntityType.PACKAGE.idPrefix),
            weight = weight,
            priority = priority,
            originWarehouse = originWarehouse,
            destinationWarehouse = destinationWarehouse
        )

        return when (val result = validator(pkg)) {
            is ValidationResult.Valid -> { packageRepository.create(pkg) }

            is ValidationResult.Invalid -> { throw EntityValidationException(result.violations) }
        }
    }
}
/*

    suspend operator fun invoke(
        weight: Double,
        priority: Priority,
        originWarehouse: Warehouse,
        destinationWarehouse: Warehouse
    ): Package {

        val pkg = Package(
            id = idGenerator.next(EntityType.PACKAGE.idPrefix),
            weight = weight,
            priority = priority,
            originWarehouse = originWarehouse,
            destinationWarehouse = destinationWarehouse
        )

        return when (val result = validator(pkg)) {

            is ValidationResult.Valid -> {
                packageRepository.create(pkg)
            }

            is ValidationResult.Invalid -> {
                throw EntityValidationException(result.violations)
            }
        }
    }
}
 */