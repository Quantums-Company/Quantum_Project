package org.bytebloom.domain.usecase.crud.packages

import org.bytebloom.domain.repository.PackageRepository

class DeletePackageUseCase(
    private val packageRepository: PackageRepository
) {
    operator fun invoke(id: String): Boolean {
        return packageRepository.delete(id)
    }
}