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
import org.bytebloom.logiroute.domain.model.exception.ResourceNotFoundException
import org.bytebloom.logiroute.domain.model.input.update.PackageUpdateInput
import org.bytebloom.logiroute.domain.repository.PackageRepository
import org.bytebloom.logiroute.domain.validator.update.UpdatePackageValidator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class UpdatePackageUseCaseTest {

    private lateinit var packageRepository: PackageRepository
    private lateinit var updatePackage: UpdatePackageUseCase

    @BeforeEach
    fun setUp() {
        packageRepository = mockk()
        updatePackage = UpdatePackageUseCase(packageRepository, UpdatePackageValidator())
    }

    @Test
    fun `should update only provided fields and keep the rest when package exists`() = runTest {
        // Given
        val input = PackageUpdateInput(id = PACKAGE_ID, weight = UPDATED_WEIGHT_KG)
        coEvery { packageRepository.getById(PACKAGE_ID) } returns createPackage()
        coEvery { packageRepository.update(any()) } answers { firstArg() }

        // When
        val result = updatePackage(input)

        // Then
        assertThat(result.weight).isEqualTo(UPDATED_WEIGHT_KG)
        assertThat(result.priority).isEqualTo(Priority.STANDARD)
        assertThat(result.destinationWarehouse).isEqualTo(DESTINATION)
    }

    @Test
    fun `should throw not found exception and skip update when package does not exist`() = runTest {
        // Given
        val input = PackageUpdateInput(id = MISSING_ID, weight = UPDATED_WEIGHT_KG)
        coEvery { packageRepository.getById(MISSING_ID) } returns null

        // When
        val exception = assertFailsWith<ResourceNotFoundException> { updatePackage(input) }

        // Then
        assertThat(exception).hasMessageThat().contains(MISSING_ID)
        coVerify(exactly = 0) { packageRepository.update(any()) }
    }

    @Test
    fun `should throw validation exception and skip repository when no fields are provided`() = runTest {
        // Given
        val input = PackageUpdateInput(id = PACKAGE_ID)

        // When / Then
        assertFailsWith<EntityValidationException> { updatePackage(input) }
        coVerify(exactly = 0) { packageRepository.getById(any()) }
        coVerify(exactly = 0) { packageRepository.update(any()) }
    }

    @Test
    fun `should throw validation exception and skip repository when weight is not positive`() = runTest {
        // Given
        val input = PackageUpdateInput(id = PACKAGE_ID, weight = 0.0)

        // When / Then
        assertFailsWith<EntityValidationException> { updatePackage(input) }
        coVerify(exactly = 0) { packageRepository.getById(any()) }
    }

    @Test
    fun `should throw validation exception and skip update when new destination equals existing origin`() = runTest {
        // Given
        val input = PackageUpdateInput(id = PACKAGE_ID, destinationWarehouse = ORIGIN)
        coEvery { packageRepository.getById(PACKAGE_ID) } returns createPackage()

        // When / Then
        assertFailsWith<EntityValidationException> { updatePackage(input) }
        coVerify(exactly = 0) { packageRepository.update(any()) }
    }

    @Test
    fun `should propagate network exception when repository fails`() = runTest {
        // Given
        val input = PackageUpdateInput(id = PACKAGE_ID, weight = UPDATED_WEIGHT_KG)
        coEvery { packageRepository.getById(PACKAGE_ID) } throws NetworkUnavailableException()

        // When / Then
        assertFailsWith<NetworkUnavailableException> { updatePackage(input) }
    }

    private fun createPackage() = Package(
        id = PACKAGE_ID,
        weight = ORIGINAL_WEIGHT_KG,
        priority = Priority.STANDARD,
        originWarehouse = ORIGIN,
        destinationWarehouse = DESTINATION
    )

    private companion object {
        const val PACKAGE_ID = "PKG-001"
        const val MISSING_ID = "PKG-404"
        const val ORIGINAL_WEIGHT_KG = 10.0
        const val UPDATED_WEIGHT_KG = 20.0
        val ORIGIN = createWarehouse("WH-001")
        val DESTINATION = createWarehouse("WH-002")

        fun createWarehouse(id: String) = Warehouse(
            id = id,
            name = "Test Warehouse",
            regionalZone = "CENTRAL",
            longitude = 35.0,
            latitude = 32.0
        )
    }
}