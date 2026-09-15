package org.bytebloom.domain.usecase.crud.packages

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.repository.PackageRepository

class UpdatePackageUseCase(
    private val packageRepository: PackageRepository
) {
    operator fun invoke(pkg: Package): Package {
        return packageRepository.update(pkg)
    }
}