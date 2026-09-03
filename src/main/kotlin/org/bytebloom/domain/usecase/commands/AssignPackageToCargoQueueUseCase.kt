package org.bytebloom.domain.usecase.commands

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Warehouse

class AssignPackageToCargoQueueUseCase {

    operator fun invoke(warehouse: Warehouse, pkg: Package): Boolean =
        warehouse.addPackage(pkg)
        //ToDO("We should add the pkg to data and update data")
}