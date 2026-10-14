package org.bytebloom.domain.knn

import org.bytebloom.domain.model.knn.CargoFeatures

class FeatureNormalizer(referenceFeatures: List<CargoFeatures>) {
    private val minimums: List<Double>
    private val ranges: List<Double>

    init {
        require(referenceFeatures.isNotEmpty()) { "Reference features must not be empty" }
        val vectors = referenceFeatures.map { it.toVector() }
        val dimensions = vectors.first().indices
        minimums = dimensions.map { index -> vectors.minOf { it[index] } }
        ranges = dimensions.map { index -> vectors.maxOf { it[index] } - minimums[index] }
    }

    fun normalize(features: CargoFeatures): CargoFeatures {
        val scaledVector = features.toVector().mapIndexed { index, value -> scale(value, index) }
        return CargoFeatures.fromVector(scaledVector)
    }

    private fun scale(value: Double, index: Int): Double {
        if (ranges[index] == 0.0) return 0.0
        return ((value - minimums[index]) / ranges[index]).coerceIn(0.0, 1.0)
    }
}