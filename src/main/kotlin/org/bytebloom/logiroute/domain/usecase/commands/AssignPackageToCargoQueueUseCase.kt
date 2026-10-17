package org.bytebloom.logiroute.domain.usecase.commands

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Warehouse

class AssignPackageToCargoQueueUseCase {

    operator fun invoke(warehouse: Warehouse, pkg: Package): Boolean {
        if (pkg.originWarehouse.id != warehouse.id) return false
        if (warehouse.containsPackage(pkg)) return false
        return warehouse.addPackage(pkg)
    }

}