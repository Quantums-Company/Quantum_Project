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

class FindPackagesByDestinationUseCaseTest {

    private lateinit var packageRepository: PackageRepository
    private lateinit var findPackagesByDestination: FindPackagesByDestinationUseCase

    @BeforeEach
    fun setUp() {
        packageRepository = mockk()
        findPackagesByDestination = FindPackagesByDestinationUseCase(packageRepository)
    }

    @Test
    fun `should return only packages heading to requested destination`() = runTest {
        // Given
        val toTarget = createPackage(id = "PKG-001", destination = TARGET_WAREHOUSE)
        val toOther = createPackage(id = "PKG-002", destination = OTHER_WAREHOUSE)
        coEvery { packageRepository.getAll() } returns listOf(toTarget, toOther)

        // When
        val result = findPackagesByDestination(TARGET_WAREHOUSE)

        // Then
        assertThat(result).containsExactly(toTarget)
    }

    @Test
    fun `should return empty list when no package heads to requested destination`() = runTest {
        // Given
        coEvery { packageRepository.getAll() } returns listOf(
            createPackage(id = "PKG-001", destination = OTHER_WAREHOUSE)
        )

        // When
        val result = findPackagesByDestination(TARGET_WAREHOUSE)

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `should propagate network exception when repository fails`() = runTest {
        // Given
        coEvery { packageRepository.getAll() } throws NetworkUnavailableException()

        // When / Then
        assertFailsWith<NetworkUnavailableException> { findPackagesByDestination(TARGET_WAREHOUSE) }
    }

    private fun createPackage(id: String, destination: Warehouse) = Package(
        id = id,
        weight = 10.0,
        priority = Priority.STANDARD,
        originWarehouse = ORIGIN_WAREHOUSE,
        destinationWarehouse = destination
    )

    private companion object {
        val ORIGIN_WAREHOUSE = createWarehouse("WH-001")
        val TARGET_WAREHOUSE = createWarehouse("WH-002")
        val OTHER_WAREHOUSE = createWarehouse("WH-003")

        fun createWarehouse(id: String) = Warehouse(
            id = id,
            name = "Test Warehouse",
            regionalZone = "CENTRAL",
            longitude = 35.0,
            latitude = 32.0
        )
    }
}