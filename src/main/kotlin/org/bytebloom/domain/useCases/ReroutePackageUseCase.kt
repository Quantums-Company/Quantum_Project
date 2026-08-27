package org.bytebloom.domain.useCases

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Warehouse


class ReroutePackageUseCase {
    operator fun invoke(pkg: Package, newWarehouse: Warehouse): Warehouse {
        newWarehouse.addPackage(pkg)
        return newWarehouse
    }
}
