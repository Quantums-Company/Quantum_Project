package org.bytebloom.domain.usecase.crud.packages

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.repository.PackageRepository

class GetPackageByIdUseCase(
    private val packageRepository: PackageRepository
) {
    operator fun invoke(id: String): Package? {
        return packageRepository.getById(id)
    }
}