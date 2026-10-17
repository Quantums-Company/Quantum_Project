package org.bytebloom.logiroute.domain.usecase.packages

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Priority
import org.bytebloom.logiroute.domain.repository.PackageRepository

class FindPackagesByPriorityUseCase(
    private val packageRepository: PackageRepository
) {

    suspend operator fun invoke(
        priority: Priority
    ): List<Package> =
        packageRepository
            .getAll()
            .filter { it.priority == priority }
            .sortedBy(Package::id)
}