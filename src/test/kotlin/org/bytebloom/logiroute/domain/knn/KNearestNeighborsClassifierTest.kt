package org.bytebloom.logiroute.domain.knn

import com.google.common.truth.Truth.assertThat
import org.bytebloom.logiroute.domain.model.Region
import org.bytebloom.logiroute.domain.model.knn.CargoFeatures
import org.bytebloom.logiroute.domain.model.knn.LabeledCargoRecord
import org.junit.jupiter.api.Test

class KNearestNeighborsClassifierTest {

    @Test
    fun `should classify target into the region of its nearest neighbors`() {
        // Given
        val classifier = KNearestNeighborsClassifier()
        val target = CargoFeatures(latitude = 0.2, longitude = 0.1, weightKg = 105.0)

        // When
        val region = classifier.classify(target, WEST_CLUSTER + EAST_CLUSTER)

        // Then
        assertThat(region).isEqualTo(Region.WEST)
    }

    @Test
    fun `should vote only between the k nearest neighbors`() {
        // Given
        val classifier = KNearestNeighborsClassifier(neighborCount = 3)
        val nearbyEastRecords = List(3) { labeled(latitude = 10.0, longitude = 10.0, weightKg = 100.0, Region.EAST) }
        val distantWestRecords = List(6) { labeled(latitude = 0.0, longitude = 0.0, weightKg = 100.0, Region.WEST) }
        val target = CargoFeatures(latitude = 9.8, longitude = 9.9, weightKg = 100.0)

        // When
        val region = classifier.classify(target, nearbyEastRecords + distantWestRecords)

        // Then
        assertThat(region).isEqualTo(Region.EAST)
    }

    @Test
    fun `should normalize features so large weights do not dominate the distance`() {
        // Given
        val classifier = KNearestNeighborsClassifier(neighborCount = 3)
        val westRecords = List(3) { labeled(latitude = 0.0, longitude = 0.0, weightKg = 500.0, Region.WEST) }
        val eastRecords = List(3) { labeled(latitude = 10.0, longitude = 10.0, weightKg = 520.0, Region.EAST) }
        val target = CargoFeatures(latitude = 0.5, longitude = 0.5, weightKg = 515.0)

        // When
        val region = classifier.classify(target, westRecords + eastRecords)

        // Then
        assertThat(region).isEqualTo(Region.WEST)
    }

    @Test
    fun `should fail when there are no training records`() {
        // Given
        val classifier = KNearestNeighborsClassifier()
        val target = CargoFeatures(latitude = 0.0, longitude = 0.0, weightKg = 100.0)

        // When
        val result = runCatching { classifier.classify(target, emptyList()) }

        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `should fail when neighbor count is not positive`() {
        // When
        val result = runCatching { KNearestNeighborsClassifier(neighborCount = 0) }

        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(IllegalArgumentException::class.java)
    }

    private companion object {
        val WEST_CLUSTER = listOf(
            labeled(latitude = 0.0, longitude = 0.0, weightKg = 100.0, Region.WEST),
            labeled(latitude = 0.3, longitude = 0.2, weightKg = 110.0, Region.WEST),
            labeled(latitude = 0.1, longitude = 0.4, weightKg = 95.0, Region.WEST),
            labeled(latitude = 0.4, longitude = 0.1, weightKg = 120.0, Region.WEST),
            labeled(latitude = 0.2, longitude = 0.3, weightKg = 105.0, Region.WEST)
        )

        val EAST_CLUSTER = listOf(
            labeled(latitude = 10.0, longitude = 10.0, weightKg = 400.0, Region.EAST),
            labeled(latitude = 10.2, longitude = 9.8, weightKg = 420.0, Region.EAST),
            labeled(latitude = 9.9, longitude = 10.3, weightKg = 390.0, Region.EAST)
        )

        fun labeled(latitude: Double, longitude: Double, weightKg: Double, region: Region) =
            LabeledCargoRecord(CargoFeatures(latitude, longitude, weightKg), region)
    }
}