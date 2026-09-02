package org.bytebloom.domain.usecase.queries

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.repository.PackageRepository

class FindPackagesByPriorityUseCase(
    private val packageRepository: PackageRepository
) {

    operator fun invoke(
        priority: Priority
    ): List<Package> =
        packageRepository
            .getAll()
            .filter { it.priority == priority }
            .sortedBy(Package::id)
}