package org.bytebloom.logiroute.domain.usecase.dispatch

import com.google.common.truth.Truth.assertThat
import org.bytebloom.logiroute.domain.knapsack.KnapsackCargoOptimizer
import org.bytebloom.logiroute.domain.model.CargoItem
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Priority
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OptimizeCargoLoadUseCaseTest {

    private lateinit var optimizeCargoLoad: OptimizeCargoLoadUseCase

    @BeforeEach
    fun setUp() {
        optimizeCargoLoad = OptimizeCargoLoadUseCase(KnapsackCargoOptimizer())
    }

    @Test
    fun `should return empty result when there are no candidate packages`() {
        // Given
        val vehicle = createVehicle(capacityKg = 10.0)

        // When
        val result = optimizeCargoLoad(vehicle, emptyList())

        // Then
        assertThat(result.selectedItems).isEmpty()
        assertThat(result.totalWeightKg).isEqualTo(0)
        assertThat(result.totalPriorityValue).isEqualTo(0)
    }

    @Test
    fun `should map package weight and priority score into selected cargo item`() {
        // Given
        val vehicle = createVehicle(capacityKg = 5.0)
        val urgentPackage = createPackage(id = "PKG-001", weightKg = 5.0, priority = Priority.URGENT)

        // When
        val result = optimizeCargoLoad(vehicle, listOf(urgentPackage))

        // Then
        assertThat(result.selectedItems)
            .containsExactly(CargoItem(id = "PKG-001", weightKg = 5, priorityValue = Priority.URGENT.score))
    }

    @Test
    fun `should round fractional package weight to nearest kilogram`() {
        // Given
        val vehicle = createVehicle(capacityKg = 5.0)
        val fractionalPackage = createPackage(id = "PKG-001", weightKg = 4.6, priority = Priority.STANDARD)

        // When
        val result = optimizeCargoLoad(vehicle, listOf(fractionalPackage))

        // Then
        assertThat(result.selectedItems.single().weightKg).isEqualTo(5)
    }

    @Test
    fun `should select every package when all fit in vehicle`() {
        // Given
        val vehicle = createVehicle(capacityKg = 10.0)
        val packages = listOf(
            createPackage(id = "PKG-001", weightKg = 2.0, priority = Priority.STANDARD),
            createPackage(id = "PKG-002", weightKg = 3.0, priority = Priority.LOW)
        )

        // When
        val result = optimizeCargoLoad(vehicle, packages)

        // Then
        assertThat(result.selectedItems.map { it.id }).containsExactly("PKG-001", "PKG-002")
        assertThat(result.totalWeightKg).isEqualTo(5)
        assertThat(result.totalPriorityValue).isEqualTo(Priority.STANDARD.score + Priority.LOW.score)
    }

    @Test
    fun `should exclude package when it is heavier than vehicle capacity`() {
        // Given
        val vehicle = createVehicle(capacityKg = 5.0)
        val heavyPackage = createPackage(id = "PKG-001", weightKg = 20.0, priority = Priority.URGENT)

        // When
        val result = optimizeCargoLoad(vehicle, listOf(heavyPackage))

        // Then
        assertThat(result.selectedItems).isEmpty()
    }

    @Test
    fun `should maximise total priority instead of picking greedily`() {
        // Given
        val vehicle = createVehicle(capacityKg = 50.0)
        val packages = listOf(
            createPackage(id = "PKG-001", weightKg = 10.0, priority = Priority.LOW),
            createPackage(id = "PKG-002", weightKg = 20.0, priority = Priority.URGENT),
            createPackage(id = "PKG-003", weightKg = 30.0, priority = Priority.URGENT)
        )

        // When
        val result = optimizeCargoLoad(vehicle, packages)

        // Then
        assertThat(result.selectedItems.map { it.id }).containsExactly("PKG-002", "PKG-003")
        assertThat(result.totalPriorityValue).isEqualTo(2 * Priority.URGENT.score)
    }

    @Test
    fun `should exclude bulky package when volume limit is reached`() {
        // Given
        val vehicle = createVehicle(capacityKg = 100.0)
        val bulkyPackage = createPackage(id = "PKG-001", weightKg = 10.0, priority = Priority.URGENT)
        val compactPackage = createPackage(id = "PKG-002", weightKg = 10.0, priority = Priority.STANDARD)
        val volumeByPackageId = mapOf("PKG-001" to 2.0, "PKG-002" to 0.5)

        // When
        val result = optimizeCargoLoad.optimizeWithVolume(
            vehicle = vehicle,
            candidatePackages = listOf(bulkyPackage, compactPackage),
            volumeByPackageId = volumeByPackageId,
            maxVolumeM3 = 1.0
        )

        // Then
        assertThat(result.selectedItems.map { it.id }).containsExactly("PKG-002")
    }

    private fun createVehicle(capacityKg: Double) = Vehicle(
        id = "TRK-001",
        maxCapacityKg = capacityKg,
        costPerKm = 2.0,
        currentWarehouse = ORIGIN
    )

    private fun createPackage(id: String, weightKg: Double, priority: Priority) = Package(
        id = id,
        weight = weightKg,
        priority = priority,
        originWarehouse = ORIGIN,
        destinationWarehouse = DESTINATION
    )

    private companion object {
        val ORIGIN = createWarehouse("WH-001")
        val DESTINATION = createWarehouse("WH-002")

        fun createWarehouse(id: String) = Warehouse(
            id = id,
            name = "Hub $id",
            regionalZone = "CENTRAL",
            longitude = 34.0,
            latitude = 31.0
        )
    }
}