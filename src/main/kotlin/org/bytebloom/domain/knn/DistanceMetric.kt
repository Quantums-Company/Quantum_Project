package org.bytebloom.domain.knn

import org.bytebloom.domain.model.knn.CargoFeatures

fun interface DistanceMetric {
    fun between(first: CargoFeatures, second: CargoFeatures): Double
}