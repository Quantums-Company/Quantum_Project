package org.bytebloom.logiroute.domain.knn

import org.bytebloom.logiroute.domain.model.knn.CargoFeatures
import kotlin.math.sqrt

class EuclideanDistance : DistanceMetric {
    override fun between(first: CargoFeatures, second: CargoFeatures): Double {
        val squaredDifferencesSum = first.toVector()
            .zip(second.toVector())
            .sumOf { (firstValue, secondValue) -> square(firstValue - secondValue) }
        return sqrt(squaredDifferencesSum)
    }

    private fun square(value: Double): Double = value * value
}