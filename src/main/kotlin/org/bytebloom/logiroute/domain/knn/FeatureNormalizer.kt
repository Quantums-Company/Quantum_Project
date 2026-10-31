package org.bytebloom.logiroute.domain.knn

import org.bytebloom.logiroute.domain.model.exception.InvalidConfigurationException
import org.bytebloom.logiroute.domain.model.knn.CargoFeatures

class FeatureNormalizer(referenceFeatures: List<CargoFeatures>) {
    private val minimums: List<Double>
    private val ranges: List<Double>

    init {
        if (referenceFeatures.isEmpty()) {
            throw InvalidConfigurationException(
                "Feature normalization requires at least one reference record."
            )
        }

        val vectors = referenceFeatures.map { it.toVector() }
        val dimensions = vectors.first().indices

        minimums = dimensions.map { featureIndex -> vectors.minOf { it[featureIndex] } }
        ranges = dimensions.map { featureIndex ->
            vectors.maxOf { it[featureIndex] } - minimums[featureIndex]
        }
    }

    fun normalize(features: CargoFeatures): CargoFeatures {
        val scaledVector = features.toVector().mapIndexed { featureIndex, featureValue ->
            scale(featureValue, featureIndex)
        }
        return CargoFeatures.fromVector(scaledVector)
    }

    private fun scale(featureValue: Double, featureIndex: Int): Double {
        if (ranges[featureIndex] == 0.0) return 0.0
        return ((featureValue - minimums[featureIndex]) / ranges[featureIndex]).coerceIn(0.0, 1.0)
    }
}