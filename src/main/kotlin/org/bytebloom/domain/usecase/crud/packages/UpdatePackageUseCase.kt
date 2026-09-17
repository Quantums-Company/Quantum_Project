package org.bytebloom.domain.usecase.crud.packages

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.validator.UpdatePackageValidator
import org.bytebloom.domain.validator.ValidationResult

class UpdatePackageUseCase(
    private val packageRepository: PackageRepository,
    private val validator: UpdatePackageValidator
) {
    suspend operator fun invoke(pkg: Package): Package {

        when (val result = validator(pkg)) {
            is ValidationResult.Valid -> {
                return packageRepository.update(pkg)
            }

            is ValidationResult.Invalid -> {
                throw IllegalArgumentException(
                    result.violations.joinToString(", ")
                )
            }
        }
    }
}