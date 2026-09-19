package org.bytebloom.domain.usecase.crud.packages

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.validator.CreatePackageValidator
import org.bytebloom.domain.validator.ValidationResult
import org.bytebloom.domain.exception.EntityValidationException

class CreatePackageUseCase(
    private val packageRepository: PackageRepository,
    private val validator: CreatePackageValidator
) {
    suspend operator fun invoke(pkg: Package): Package {

        when (val result = validator(pkg)) {
            is ValidationResult.Valid -> {
                return packageRepository.create(pkg)
            }

            is ValidationResult.Invalid -> {
                throw EntityValidationException(result.violations)
            }
        }
    }
}