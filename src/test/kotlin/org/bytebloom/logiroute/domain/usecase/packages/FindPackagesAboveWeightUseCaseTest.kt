package org.bytebloom.logiroute.domain.usecase.packages

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Priority
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.exception.NetworkUnavailableException
import org.bytebloom.logiroute.domain.repository.PackageRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class FindPackagesAboveWeightUseCaseTest {

    private lateinit var packageRepository: PackageRepository
    private lateinit var findPackagesAboveWeight: FindPackagesAboveWeightUseCase

    @BeforeEach
    fun setUp() {
        packageRepository = mockk()
        findPackagesAboveWeight = FindPackagesAboveWeightUseCase(packageRepository)
    }

    @Test
    fun `should return only packages heavier than minimum weight`() = runTest {
        // Given
        val lightPackage = createPackage(id = "PKG-001", weight = 50.0)
        val heavyPackage = createPackage(id = "PKG-002", weight = 150.0)
        coEvery { packageRepository.getAll() } returns listOf(lightPackage, heavyPackage)

        // When
        val result = findPackagesAboveWeight(MINIMUM_WEIGHT_KG)

        // Then
        assertThat(result).containsExactly(heavyPackage)
    }

    @Test
    fun `should include package when weight equals minimum weight`() = runTest {
        // Given
        val boundaryPackage = createPackage(id = "PKG-001", weight = MINIMUM_WEIGHT_KG)
        coEvery { packageRepository.getAll() } returns listOf(boundaryPackage)

        // When
        val result = findPackagesAboveWeight(MINIMUM_WEIGHT_KG)

        // Then
        assertThat(result).containsExactly(boundaryPackage)
    }

    @Test
    fun `should return empty list when no package reaches minimum weight`() = runTest {
        // Given
        coEvery { packageRepository.getAll() } returns listOf(createPackage(id = "PKG-001", weight = 10.0))

        // When
        val result = findPackagesAboveWeight(MINIMUM_WEIGHT_KG)

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `should propagate network exception when repository fails`() = runTest {
        // Given
        coEvery { packageRepository.getAll() } throws NetworkUnavailableException()

        // When / Then
        assertFailsWith<NetworkUnavailableException> { findPackagesAboveWeight(MINIMUM_WEIGHT_KG) }
    }

    private fun createPackage(id: String, weight: Double) = Package(
        id = id,
        weight = weight,
        priority = Priority.STANDARD,
        originWarehouse = createWarehouse("WH-001"),
        destinationWarehouse = createWarehouse("WH-002")
    )

    private fun createWarehouse(id: String) = Warehouse(
        id = id,
        name = "Test Warehouse",
        regionalZone = "CENTRAL",
        longitude = 35.0,
        latitude = 32.0
    )

    private companion object {
        const val MINIMUM_WEIGHT_KG = 100.0
    }
}