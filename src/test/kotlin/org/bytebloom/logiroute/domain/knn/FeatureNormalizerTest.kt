package org.bytebloom.logiroute.domain.knn

import com.google.common.truth.Truth.assertThat
import org.bytebloom.logiroute.domain.knn.FeatureNormalizer
import org.bytebloom.logiroute.domain.model.knn.CargoFeatures
import org.junit.jupiter.api.Test

class FeatureNormalizerTest {

    @Test
    fun `should scale smallest reference values to zero`() {
        // Given
        val normalizer = FeatureNormalizer(REFERENCE_FEATURES)

        // When
        val normalized = normalizer.normalize(SMALLEST_FEATURES)

        // Then
        assertThat(normalized).isEqualTo(CargoFeatures(latitude = 0.0, longitude = 0.0, weightKg = 0.0))
    }

    @Test
    fun `should scale largest reference values to one`() {
        // Given
        val normalizer = FeatureNormalizer(REFERENCE_FEATURES)

        // When
        val normalized = normalizer.normalize(LARGEST_FEATURES)

        // Then
        assertThat(normalized).isEqualTo(CargoFeatures(latitude = 1.0, longitude = 1.0, weightKg = 1.0))
    }

    @Test
    fun `should scale middle values proportionally`() {
        // Given
        val normalizer = FeatureNormalizer(REFERENCE_FEATURES)

        // When
        val normalized = normalizer.normalize(CargoFeatures(latitude = 15.0, longitude = -90.0, weightKg = 300.0))

        // Then
        assertThat(normalized).isEqualTo(CargoFeatures(latitude = 0.5, longitude = 0.5, weightKg = 0.5))
    }

    @Test
    fun `should keep values outside the reference range between zero and one`() {
        // Given
        val normalizer = FeatureNormalizer(REFERENCE_FEATURES)

        // When
        val normalized = normalizer.normalize(CargoFeatures(latitude = 50.0, longitude = -200.0, weightKg = 900.0))

        // Then
        assertThat(normalized).isEqualTo(CargoFeatures(latitude = 1.0, longitude = 0.0, weightKg = 1.0))
    }

    @Test
    fun `should return zero for a feature that has the same value in every reference`() {
        // Given
        val sameWeightFeatures = listOf(
            CargoFeatures(latitude = 10.0, longitude = -100.0, weightKg = 250.0),
            CargoFeatures(latitude = 20.0, longitude = -80.0, weightKg = 250.0)
        )
        val normalizer = FeatureNormalizer(sameWeightFeatures)

        // When
        val normalized = normalizer.normalize(CargoFeatures(latitude = 15.0, longitude = -90.0, weightKg = 250.0))

        // Then
        assertThat(normalized.weightKg).isEqualTo(0.0)
    }

    @Test
    fun `should fail when there are no reference features`() {
        // When
        val result = runCatching { FeatureNormalizer(emptyList()) }

        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(IllegalArgumentException::class.java)
    }

    private companion object {
        val SMALLEST_FEATURES = CargoFeatures(latitude = 10.0, longitude = -100.0, weightKg = 100.0)
        val LARGEST_FEATURES = CargoFeatures(latitude = 20.0, longitude = -80.0, weightKg = 500.0)
        val REFERENCE_FEATURES = listOf(SMALLEST_FEATURES, LARGEST_FEATURES)
    }
}