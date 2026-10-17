package org.bytebloom.logiroute.domain.usecase.packages

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.model.EntityType
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Priority
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.model.exception.NetworkUnavailableException
import org.bytebloom.logiroute.domain.repository.PackageRepository
import org.bytebloom.logiroute.domain.service.IdGenerator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class CreatePackageUseCaseTest {

    private lateinit var packageRepository: PackageRepository
    private lateinit var idGenerator: IdGenerator
    private lateinit var createPackage: CreatePackageUseCase

    @BeforeEach
    fun setUp() {
        packageRepository = mockk()
        idGenerator = mockk()
        every { idGenerator.next(EntityType.PACKAGE) } returns PACKAGE_ID
        createPackage = CreatePackageUseCase(packageRepository, idGenerator)
    }

    @Test
    fun `should store package with generated id when inputs are valid`() = runTest {
        // Given
        val storedPackage = createPackage()
        coEvery { packageRepository.create(any()) } returns storedPackage

        // When
        val result = createPackage(WEIGHT_KG, Priority.URGENT, ORIGIN, DESTINATION)

        // Then
        assertThat(result).isSameInstanceAs(storedPackage)
        coVerify(exactly = 1) {
            packageRepository.create(match { it.id == PACKAGE_ID && it.weight == WEIGHT_KG })
        }
    }

    @Test
    fun `should throw validation exception and skip repository when weight is negative`() = runTest {
        // Given
        val negativeWeight = -5.0

        // When / Then
        assertFailsWith<EntityValidationException> {
            createPackage(negativeWeight, Priority.STANDARD, ORIGIN, DESTINATION)
        }
        coVerify(exactly = 0) { packageRepository.create(any()) }
    }

    @Test
    fun `should throw validation exception and skip repository when origin equals destination`() = runTest {
        // Given
        val sameWarehouse = ORIGIN

        // When / Then
        assertFailsWith<EntityValidationException> {
            createPackage(WEIGHT_KG, Priority.STANDARD, sameWarehouse, sameWarehouse)
        }
        coVerify(exactly = 0) { packageRepository.create(any()) }
    }

    @Test
    fun `should propagate network exception when repository fails`() = runTest {
        // Given
        coEvery { packageRepository.create(any()) } throws NetworkUnavailableException()

        // When / Then
        assertFailsWith<NetworkUnavailableException> {
            createPackage(WEIGHT_KG, Priority.STANDARD, ORIGIN, DESTINATION)
        }
    }

    private fun createPackage() = Package(
        id = PACKAGE_ID,
        weight = WEIGHT_KG,
        priority = Priority.URGENT,
        originWarehouse = ORIGIN,
        destinationWarehouse = DESTINATION
    )

    private companion object {
        const val PACKAGE_ID = "PKG-001"
        const val WEIGHT_KG = 12.5
        val ORIGIN = createWarehouse("WH-001", "Amman Hub")
        val DESTINATION = createWarehouse("WH-002", "Irbid Depot")

        fun createWarehouse(id: String, name: String) = Warehouse(
            id = id,
            name = name,
            regionalZone = "CENTRAL",
            longitude = 35.0,
            latitude = 32.0
        )
    }
}