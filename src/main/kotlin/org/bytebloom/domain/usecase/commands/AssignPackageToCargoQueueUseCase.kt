package org.bytebloom.domain.usecase.commands

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Warehouse

class AssignPackageToCargoQueueUseCase {

    operator fun invoke(warehouse: Warehouse, pkg: Package): Boolean {
        if (pkg.originWarehouse.id != warehouse.id) return false
        if (warehouse.containsPackage(pkg)) return false
        return warehouse.addPackage(pkg)
    }

}