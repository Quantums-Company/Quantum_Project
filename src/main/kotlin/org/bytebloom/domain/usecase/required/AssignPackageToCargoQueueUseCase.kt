package org.bytebloom.domain.usecase.required

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Warehouse

class AssignPackageToCargoQueueUseCase {

    operator fun invoke(warehouse: Warehouse, pkg: Package): Warehouse {
        warehouse.addPackage(pkg)
        warehouse.sortCargoByWeight()

        return warehouse
    }
}