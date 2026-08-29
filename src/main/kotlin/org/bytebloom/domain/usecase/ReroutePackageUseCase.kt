package org.bytebloom.domain.usecase

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Warehouse


class ReroutePackageUseCase {
    operator fun invoke(pkg: Package, warehouse: Warehouse): Warehouse {
        warehouse.addPackage(pkg)
        return warehouse
    }
}
