package org.bytebloom.domain.usecase

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.repository.PackageRepository

class FindPackagesAboveWeightUseCase(
    private val packageRepository: PackageRepository
) {

    operator fun invoke(minimumWeightKg: Double): List<Package> {

        return packageRepository
            .getAll()
            .filter { it.weight > minimumWeightKg }
    }
}