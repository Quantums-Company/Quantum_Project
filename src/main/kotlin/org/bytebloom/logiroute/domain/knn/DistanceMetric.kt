package org.bytebloom.logiroute.domain.knn

import org.bytebloom.logiroute.domain.model.knn.CargoFeatures

fun interface DistanceMetric {
    fun between(first: CargoFeatures, second: CargoFeatures): Double
}