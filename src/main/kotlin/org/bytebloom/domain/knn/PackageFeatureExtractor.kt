package org.bytebloom.domain.knn

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.knn.CargoFeatures

fun Package.toCargoFeatures(): CargoFeatures =
    CargoFeatures(
        latitude = destinationWarehouse.latitude,
        longitude = destinationWarehouse.longitude,
        weightKg = weight
    )