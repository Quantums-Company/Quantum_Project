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

class FindPackagesByPriorityUseCaseTest {

    private lateinit var packageRepository: PackageRepository
    private lateinit var findPackagesByPriority: FindPackagesByPriorityUseCase

    @BeforeEach
    fun setUp() {
        packageRepository = mockk()
        findPackagesByPriority = FindPackagesByPriorityUseCase(packageRepository)
    }

    @Test
    fun `should return only packages with requested priority sorted by id`() = runTest {
        // Given
        val secondUrgent = createPackage(id = "PKG-002", priority = Priority.URGENT)
        val standard = createPackage(id = "PKG-003", priority = Priority.STANDARD)
        val firstUrgent = createPackage(id = "PKG-001", priority = Priority.URGENT)
        coEvery { packageRepository.getAll() } returns listOf(secondUrgent, standard, firstUrgent)

        // When
        val result = findPackagesByPriority(Priority.URGENT)

        // Then
        assertThat(result).containsExactly(firstUrgent, secondUrgent).inOrder()
    }

    @Test
    fun `should return empty list when no package has requested priority`() = runTest {
        // Given
        coEvery { packageRepository.getAll() } returns listOf(
            createPackage(id = "PKG-001", priority = Priority.LOW)
        )

        // When
        val result = findPackagesByPriority(Priority.URGENT)

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `should propagate network exception when repository fails`() = runTest {
        // Given
        coEvery { packageRepository.getAll() } throws NetworkUnavailableException()

        // When / Then
        assertFailsWith<NetworkUnavailableException> { findPackagesByPriority(Priority.URGENT) }
    }

    private fun createPackage(id: String, priority: Priority) = Package(
        id = id,
        weight = 10.0,
        priority = priority,
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
}