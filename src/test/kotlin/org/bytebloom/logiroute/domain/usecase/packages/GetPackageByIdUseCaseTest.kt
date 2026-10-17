package org.bytebloom.logiroute.domain.usecase.packages

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Priority
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.model.exception.NetworkUnavailableException
import org.bytebloom.logiroute.domain.repository.PackageRepository
import org.bytebloom.logiroute.domain.validator.id.PackageIdValidator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertFailsWith

class GetPackageByIdUseCaseTest {

    private lateinit var packageRepository: PackageRepository
    private lateinit var getPackageById: GetPackageByIdUseCase

    @BeforeEach
    fun setUp() {
        packageRepository = mockk()
        getPackageById = GetPackageByIdUseCase(packageRepository, PackageIdValidator())
    }

    @Test
    fun `should return package when it exists in repository`() = runTest {
        // Given
        val existingPackage = createPackage()
        coEvery { packageRepository.getById(PACKAGE_ID) } returns existingPackage

        // When
        val result = getPackageById(PACKAGE_ID)

        // Then
        assertThat(result).isSameInstanceAs(existingPackage)
    }

    @Test
    fun `should return null when package does not exist`() = runTest {
        // Given
        coEvery { packageRepository.getById(MISSING_ID) } returns null

        // When
        val result = getPackageById(MISSING_ID)

        // Then
        assertThat(result).isNull()
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "WH-001", "PKG-12", "pkg-001"])
    fun `should throw validation exception and skip repository when id is invalid`(invalidId: String) = runTest {
        // Given: invalidId from @ValueSource

        // When / Then
        assertFailsWith<EntityValidationException> { getPackageById(invalidId) }
        coVerify(exactly = 0) { packageRepository.getById(any()) }
    }

    @Test
    fun `should propagate network exception when repository fails`() = runTest {
        // Given
        coEvery { packageRepository.getById(PACKAGE_ID) } throws NetworkUnavailableException()

        // When / Then
        assertFailsWith<NetworkUnavailableException> { getPackageById(PACKAGE_ID) }
    }

    private fun createPackage() = Package(
        id = PACKAGE_ID,
        weight = 10.0,
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
        const val PACKAGE_ID = "PKG-001"
        const val MISSING_ID = "PKG-999"
    }
}