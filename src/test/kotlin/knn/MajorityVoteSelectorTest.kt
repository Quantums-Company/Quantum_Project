package knn

import com.google.common.truth.Truth.assertThat
import org.bytebloom.logiroute.domain.knn.MajorityVoteSelector
import org.bytebloom.logiroute.domain.model.Region
import org.bytebloom.logiroute.domain.model.knn.CargoFeatures
import org.bytebloom.logiroute.domain.model.knn.LabeledCargoRecord
import org.junit.jupiter.api.Test

class MajorityVoteSelectorTest {

    private val majorityVoteSelector = MajorityVoteSelector()

    @Test
    fun `should select the sector with the most votes`() {
        // Given
        val neighbors = recordsIn(Region.EAST, Region.WEST, Region.WEST, Region.NORTH, Region.WEST)

        // When
        val sector = majorityVoteSelector.select(neighbors)

        // Then
        assertThat(sector).isEqualTo(Region.WEST)
    }

    @Test
    fun `should select the sector of the nearest neighbor when votes are tied`() {
        // Given
        val neighbors = recordsIn(Region.EAST, Region.WEST, Region.WEST, Region.EAST, Region.NORTH)

        // When
        val sector = majorityVoteSelector.select(neighbors)

        // Then
        assertThat(sector).isEqualTo(Region.EAST)
    }

    @Test
    fun `should select the only neighbor sector when there is one neighbor`() {
        // Given
        val neighbors = recordsIn(Region.SOUTH)

        // When
        val sector = majorityVoteSelector.select(neighbors)

        // Then
        assertThat(sector).isEqualTo(Region.SOUTH)
    }

    @Test
    fun `should fail when there are no neighbors`() {
        // When
        val result = runCatching { majorityVoteSelector.select(emptyList()) }

        // Then
        assertThat(result.exceptionOrNull()).isInstanceOf(IllegalArgumentException::class.java)
    }

    private fun recordsIn(vararg sectors: Region): List<LabeledCargoRecord> =
        sectors.map { sector -> LabeledCargoRecord(ANY_FEATURES, sector) }

    private companion object {
        val ANY_FEATURES = CargoFeatures(latitude = 0.0, longitude = 0.0, weightKg = 0.0)
    }
}