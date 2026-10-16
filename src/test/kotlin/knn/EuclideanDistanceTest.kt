package knn

import com.google.common.truth.Truth.assertThat
import org.bytebloom.domain.knn.EuclideanDistance
import org.bytebloom.domain.model.knn.CargoFeatures
import org.junit.jupiter.api.Test

class EuclideanDistanceTest {

    private val euclideanDistance = EuclideanDistance()

    @Test
    fun `should return zero for identical features`() {
        // Given
        val features = CargoFeatures(latitude = 0.3, longitude = 0.7, weightKg = 0.5)

        // When
        val distance = euclideanDistance.between(features, features)

        // Then
        assertThat(distance).isEqualTo(0.0)
    }

    @Test
    fun `should return square root of summed squared differences`() {
        // Given
        val first = CargoFeatures(latitude = 0.0, longitude = 0.0, weightKg = 0.0)
        val second = CargoFeatures(latitude = 3.0, longitude = 4.0, weightKg = 0.0)

        // When
        val distance = euclideanDistance.between(first, second)

        // Then
        assertThat(distance).isEqualTo(5.0)
    }

    @Test
    fun `should include every feature in the distance`() {
        // Given
        val first = CargoFeatures(latitude = 1.0, longitude = 2.0, weightKg = 3.0)
        val second = CargoFeatures(latitude = 2.0, longitude = 4.0, weightKg = 5.0)

        // When
        val distance = euclideanDistance.between(first, second)

        // Then
        assertThat(distance).isEqualTo(3.0)
    }

    @Test
    fun `should return the same distance in both directions`() {
        // Given
        val first = CargoFeatures(latitude = 0.1, longitude = 0.9, weightKg = 0.4)
        val second = CargoFeatures(latitude = 0.6, longitude = 0.2, weightKg = 0.8)

        // When
        val forwardDistance = euclideanDistance.between(first, second)
        val backwardDistance = euclideanDistance.between(second, first)

        // Then
        assertThat(forwardDistance).isEqualTo(backwardDistance)
    }
}