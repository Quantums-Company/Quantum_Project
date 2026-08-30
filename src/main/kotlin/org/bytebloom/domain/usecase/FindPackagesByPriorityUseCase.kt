package org.bytebloom.domain.usecase

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority

class FindPackagesByPriorityUseCase {
    operator fun invoke(packages: List<Package>, targetPriority: Priority): List<Package> {
        return packages
            .filter { it.priority == targetPriority }
            .sortedBy { it.id }
    }
}