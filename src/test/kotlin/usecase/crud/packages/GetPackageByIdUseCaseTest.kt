package usecase.crud.packages

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.usecase.crud.packages.GetPackageByIdUseCase
import org.bytebloom.domain.validator.id.PackageIdValidator
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertSame

class GetPackageByIdUseCaseTest {

    private val packageRepository = mockk<PackageRepository>()
    private val validator = mockk<PackageIdValidator>()

    private val useCase = GetPackageByIdUseCase(
        packageRepository = packageRepository,
        validator = validator
    )

    @Test
    fun `returns package when id is valid and package exists`() {
        // Given
        val id = "PKG-001"
        val expectedPackage = packageItem()

        every { validator(id) } returns ValidatorResult.Valid
        coEvery { packageRepository.getById(id) } returns expectedPackage

        // When
        val result = runBlocking {
            useCase(id)
        }

        // Then
        assertSame(expectedPackage, result)

        verify(exactly = 1) {
            validator(id)
        }

        coVerify(exactly = 1) {
            packageRepository.getById(id)
        }
    }

    @Test
    fun `returns null when package does not exist`() {
        // Given
        val id = "PKG-999"

        every { validator(id) } returns ValidatorResult.Valid
        coEvery { packageRepository.getById(id) } returns null

        // When
        val result = runBlocking {
            useCase(id)
        }

        // Then
        assertNull(result)

        coVerify(exactly = 1) {
            packageRepository.getById(id)
        }
    }

    @Test
    fun `throws validation exception when package id is invalid`() {
        // Given
        val invalidId = "WH-001"

        every {
            validator(invalidId)
        } returns ValidatorResult.Invalid(
            listOf(
                ValidatorError.InvalidIdFormat(
                    field = ValidatorField.ID,
                    entityType = EntityType.PACKAGE
                )
            )
        )

        // When
        val exception = assertFailsWith<EntityValidationException> {
            runBlocking {
                useCase(invalidId)
            }
        }

        // Then
        assertEquals(
            ValidatorField.ID,
            exception.violations.single().field
        )

        coVerify(exactly = 0) {
            packageRepository.getById(invalidId)
        }
    }

    private fun packageItem(): Package =
        Package(
            id = "PKG-001",
            weight = 10.0,
            priority = Priority.STANDARD,
            originWarehouse = warehouse("WH-001"),
            destinationWarehouse = warehouse("WH-002")
        )

    private fun warehouse(id: String): Warehouse =
        Warehouse(
            id = id,
            name = "Test Warehouse",
            regionalZone = "CENTRAL",
            longitude = 35.0,
            latitude = 32.0
        )
}