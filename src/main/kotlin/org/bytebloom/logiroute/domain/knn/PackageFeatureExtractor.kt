package org.bytebloom.logiroute.domain.knn

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.knn.CargoFeatures

fun Package.toCargoFeatures(): CargoFeatures =
    CargoFeatures(
        latitude = destinationWarehouse.latitude,
        longitude = destinationWarehouse.longitude,
        weightKg = weight
    )