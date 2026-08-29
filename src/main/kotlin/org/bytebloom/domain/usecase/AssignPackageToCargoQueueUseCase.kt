package org.bytebloom.domain.usecase

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.sorting.quickSortCargoByWeight

class AssignPackageToCargoQueueUseCase {

    operator fun invoke(warehouse: Warehouse, pkg: Package): Warehouse {
        warehouse.addPackage(pkg)
        warehouse.sortCargoByWeight()

        return warehouse
    }
}