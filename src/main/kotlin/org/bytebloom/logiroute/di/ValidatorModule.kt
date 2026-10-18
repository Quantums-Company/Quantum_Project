package org.bytebloom.logiroute.di

import org.bytebloom.logiroute.domain.validator.id.EntityIdValidator
import org.bytebloom.logiroute.domain.validator.id.PackageIdValidator
import org.bytebloom.logiroute.domain.validator.id.RouteIdValidator
import org.bytebloom.logiroute.domain.validator.id.VehicleIdValidator
import org.bytebloom.logiroute.domain.validator.id.WarehouseIdValidator
import org.bytebloom.logiroute.domain.validator.update.UpdatePackageValidator
import org.bytebloom.logiroute.domain.validator.update.UpdateRouteValidator
import org.bytebloom.logiroute.domain.validator.update.UpdateVehicleValidator
import org.bytebloom.logiroute.domain.validator.update.UpdateWarehouseValidator
import org.koin.dsl.module

val validatorModule = module {

    factory { UpdateWarehouseValidator() }
    factory { UpdatePackageValidator() }
    factory { UpdateRouteValidator() }
    factory { UpdateVehicleValidator() }

    factory { EntityIdValidator() }
    factory { WarehouseIdValidator() }
    factory { PackageIdValidator() }
    factory { RouteIdValidator() }
    factory { VehicleIdValidator() }
}