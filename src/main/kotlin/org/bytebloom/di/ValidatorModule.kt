package org.bytebloom.di

import org.bytebloom.domain.validator.id.EntityIdValidator
import org.bytebloom.domain.validator.id.PackageIdValidator
import org.bytebloom.domain.validator.id.RouteIdValidator
import org.bytebloom.domain.validator.id.VehicleIdValidator
import org.bytebloom.domain.validator.id.WarehouseIdValidator
import org.bytebloom.domain.validator.update.UpdatePackageValidator
import org.bytebloom.domain.validator.update.UpdateRouteValidator
import org.bytebloom.domain.validator.update.UpdateVehicleValidator
import org.bytebloom.domain.validator.update.UpdateWarehouseValidator
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