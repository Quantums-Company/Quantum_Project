package org.bytebloom.domain.usecase.commands

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Warehouse


class ReroutePackageUseCase {
    operator fun invoke(pkg: Package, warehouse: Warehouse): Package {
        pkg.destinationWarehouse = warehouse
        //ToDo("we should update the data")
        return pkg
    }
}
