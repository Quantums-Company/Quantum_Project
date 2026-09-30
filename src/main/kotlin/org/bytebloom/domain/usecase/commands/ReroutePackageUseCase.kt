package org.bytebloom.domain.usecase.commands

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Warehouse

class ReroutePackageUseCase {
    operator fun invoke(pkg: Package, newDestination: Warehouse): Package =
        pkg.redirectedTo(newDestination)
}