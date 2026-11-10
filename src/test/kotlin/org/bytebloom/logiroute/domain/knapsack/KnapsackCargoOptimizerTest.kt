package org.bytebloom.logiroute.domain.knapsack

import com.google.common.truth.Truth.assertThat
import org.bytebloom.logiroute.domain.model.CargoItem
import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class KnapsackCargoOptimizerTest {

    private lateinit var optimizer: KnapsackCargoOptimizer

    @BeforeEach
    fun setUp() {
        optimizer = KnapsackCargoOptimizer()
    }

    @Test
    fun `should pick optimal combination instead of greedy one when capacity is limited`() {
        // Given
        val items = listOf(
            CargoItem(id = "PKG-001", weightKg = 10, priorityValue = 60),
            CargoItem(id = "PKG-002", weightKg = 20, priorityValue = 100),
            CargoItem(id = "PKG-003", weightKg = 30, priorityValue = 120)
        )

        // When
        val result = optimizer(items = items, capacityKg = 50)

        // Then
        assertThat(result.selectedItems.map { it.id }).containsExactly("PKG-002", "PKG-003")
        assertThat(result.totalPriorityValue).isEqualTo(220)
        assertThat(result.totalWeightKg).isEqualTo(50)
    }

    @Test
    fun `should select nothing when capacity is zero`() {
        // Given
        val items = listOf(CargoItem(id = "PKG-001", weightKg = 5, priorityValue = 10))

        // When
        val result = optimizer(items = items, capacityKg = 0)

        // Then
        assertThat(result.selectedItems).isEmpty()
    }

    @Test
    fun `should include zero weight item when it has positive priority`() {
        // Given
        val freeItem = CargoItem(id = "PKG-001", weightKg = 0, priorityValue = 5)
        val fullItem = CargoItem(id = "PKG-002", weightKg = 10, priorityValue = 10)

        // When
        val result = optimizer(items = listOf(freeItem, fullItem), capacityKg = 10)

        // Then
        assertThat(result.selectedItems).contains(freeItem)
        assertThat(result.totalPriorityValue).isEqualTo(15)
    }

    @Test
    fun `should combine smaller items when they beat a single item of equal value`() {
        // Given
        val smallItem = CargoItem(id = "PKG-001", weightKg = 3, priorityValue = 10)
        val largeItem = CargoItem(id = "PKG-002", weightKg = 8, priorityValue = 10)
        val fillerItem = CargoItem(id = "PKG-003", weightKg = 5, priorityValue = 8)

        // When
        val result = optimizer(items = listOf(smallItem, largeItem, fillerItem), capacityKg = 8)

        // Then
        assertThat(result.selectedItems).containsExactly(smallItem, fillerItem)
        assertThat(result.totalPriorityValue).isEqualTo(18)
    }

    @Test
    fun `should throw validation exception when capacity is negative`() {
        // Given
        val items = listOf(CargoItem(id = "PKG-001", weightKg = 5, priorityValue = 10))

        // When / Then
        assertFailsWith<EntityValidationException> { optimizer(items = items, capacityKg = -1) }
    }

    @Test
    fun `should throw validation exception when an item weight is negative`() {
        // Given
        val items = listOf(CargoItem(id = "PKG-001", weightKg = -5, priorityValue = 10))

        // When / Then
        assertFailsWith<EntityValidationException> { optimizer(items = items, capacityKg = 10) }
    }

    @Test
    fun `should exclude bulky item when its volume exceeds volume capacity`() {
        // Given
        val bulkyItem = CargoItem(id = "PKG-001", weightKg = 5, priorityValue = 10, volumeM3 = 2.0)
        val compactItem = CargoItem(id = "PKG-002", weightKg = 5, priorityValue = 8, volumeM3 = 0.5)

        // When
        val result = optimizer(items = listOf(bulkyItem, compactItem), weightCapacityKg = 10, volumeCapacityM3 = 1.0)

        // Then
        assertThat(result.selectedItems).containsExactly(compactItem)
    }

    @Test
    fun `should exclude heavy item when its weight exceeds capacity in volume mode`() {
        // Given
        val heavyItem = CargoItem(id = "PKG-001", weightKg = 20, priorityValue = 20, volumeM3 = 0.2)
        val lightItem = CargoItem(id = "PKG-002", weightKg = 5, priorityValue = 8, volumeM3 = 0.2)

        // When
        val result = optimizer(items = listOf(heavyItem, lightItem), weightCapacityKg = 10, volumeCapacityM3 = 1.0)

        // Then
        assertThat(result.selectedItems).containsExactly(lightItem)
    }

    @Test
    fun `should prefer compact items when volume limits the payload`() {
        // Given
        val items = volumeLimitedItems()

        // When
        val result = optimizer(items = items, weightCapacityKg = 20, volumeCapacityM3 = 0.5)

        // Then
        assertThat(result.selectedItems.map { it.id }).containsExactly("PKG-002", "PKG-003")
    }

    @Test
    fun `should report totals of selected items when volume limits the payload`() {
        // Given
        val items = volumeLimitedItems()

        // When
        val result = optimizer(items = items, weightCapacityKg = 20, volumeCapacityM3 = 0.5)

        // Then
        assertThat(result.totalPriorityValue).isEqualTo(17)
        assertThat(result.totalVolumeM3).isWithin(VOLUME_TOLERANCE).of(0.4)
    }

    @Test
    fun `should reject negative volume capacity`() {
        // Given
        val items = volumeLimitedItems()

        // When / Then
        assertFailsWith<EntityValidationException> {
            optimizer(items = items, weightCapacityKg = 20, volumeCapacityM3 = -1.0)
        }
    }

    private fun volumeLimitedItems() = listOf(
        CargoItem(id = "PKG-001", weightKg = 10, priorityValue = 10, volumeM3 = 0.8),
        CargoItem(id = "PKG-002", weightKg = 10, priorityValue = 9, volumeM3 = 0.2),
        CargoItem(id = "PKG-003", weightKg = 10, priorityValue = 8, volumeM3 = 0.2)
    )

    private companion object {
        const val VOLUME_TOLERANCE = 1e-9
    }
}