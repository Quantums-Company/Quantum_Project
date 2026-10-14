package org.bytebloom.domain.model.knn

data class CargoFeatures(
    val latitude: Double,
    val longitude: Double,
    val weightKg: Double
) {
    fun toVector(): List<Double> = listOf(latitude, longitude, weightKg)

    companion object {
        fun fromVector(vector: List<Double>): CargoFeatures =
            CargoFeatures(latitude = vector[0], longitude = vector[1], weightKg = vector[2])
    }
}