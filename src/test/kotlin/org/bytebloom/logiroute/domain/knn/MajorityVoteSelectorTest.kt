package org.bytebloom.logiroute.domain.knn

import com.google.common.truth.Truth.assertThat
import org.bytebloom.logiroute.domain.model.Region
import org.bytebloom.logiroute.domain.model.knn.CargoFeatures
import org.bytebloom.logiroute.domain.model.knn.LabeledCargoRecord
import org.junit.jupiter.api.Test
import org.bytebloom.logiroute.domain.model.exception.InvalidConfigurationException

class MajorityVoteSelectorTest {

    private val majorityVoteSelector = MajorityVoteSelector()

    @Test
    fun `should select the region with the most votes`() {
        // Given
        val neighbors = recordsIn(Region.EAST, Region.WEST, Region.WEST, Region.NORTH, Region.WEST)

        // When
        val region = majorityVoteSelector.select(neighbors)

        // Then
        assertThat(region).isEqualTo(Region.WEST)
    }

    @Test
    fun `should select the region of the nearest neighbor when votes are tied`() {
        // Given
        val neighbors = recordsIn(Region.EAST, Region.WEST, Region.WEST, Region.EAST, Region.NORTH)

        // When
        val region = majorityVoteSelector.select(neighbors)

        // Then
        assertThat(region).isEqualTo(Region.EAST)
    }

    @Test
    fun `should select the only neighbor region when there is one neighbor`() {
        // Given
        val neighbors = recordsIn(Region.SOUTH)

        // When
        val region = majorityVoteSelector.select(neighbors)

        // Then
        assertThat(region).isEqualTo(Region.SOUTH)
    }

    @Test
    fun `should fail when there are no neighbors`() {
        // When
        val result = runCatching { majorityVoteSelector.select(emptyList()) }

        // Then
        assertThat(result.exceptionOrNull())
            .isInstanceOf(InvalidConfigurationException::class.java)
    }

    private fun recordsIn(vararg sectors: Region): List<LabeledCargoRecord> =
        sectors.map { region -> LabeledCargoRecord(ANY_FEATURES, region) }

    private companion object {
        val ANY_FEATURES = CargoFeatures(latitude = 0.0, longitude = 0.0, weightKg = 0.0)
    }
}