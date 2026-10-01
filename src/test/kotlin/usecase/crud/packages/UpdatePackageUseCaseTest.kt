package usecase.crud.packages

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.exception.ResourceNotFoundException
import org.bytebloom.domain.model.updateInput.PackageUpdateInput
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.usecase.crud.packages.UpdatePackageUseCase
import org.bytebloom.domain.validator.update.UpdatePackageValidator
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class UpdatePackageUseCaseTest {

    private val packageRepository = mockk<PackageRepository>()
    private val validator = mockk<UpdatePackageValidator>()

    private fun packageItem(weight: Double): Package =
        Package(
            id = "PKG-001",
            weight = weight,
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

    private val useCase = UpdatePackageUseCase(
        packageRepository = packageRepository,
        validator = validator
    )

    @Test
    fun `updates an existing package successfully`() {
        // Given
        val input = PackageUpdateInput(
            id = "PKG-001",
            weight = 20.0
        )

        val existingPackage = packageItem(weight = 10.0)
        val storedPackage = packageItem(weight = 20.0)

        every { validator(input) } returns ValidatorResult.Valid
        coEvery { packageRepository.getById(input.id) } returns existingPackage
        coEvery { packageRepository.update(any()) } returns storedPackage

        // When
        val result = runBlocking {
            useCase(input)
        }

        // Then
        assertSame(storedPackage, result)
        assertEquals(20.0, result.weight)

        coVerify(exactly = 1) {
            packageRepository.getById("PKG-001")
        }

        coVerify(exactly = 1) {
            packageRepository.update(
                match {
                    it.id == "PKG-001" &&
                            it.weight == 20.0 &&
                            it.priority == Priority.STANDARD
                }
            )
        }
    }

    @Test
    fun `throws not found exception when package does not exist`() {
        // Given
        val input = PackageUpdateInput(
            id = "PKG-404",
            weight = 20.0
        )

        every { validator(input) } returns ValidatorResult.Valid
        coEvery { packageRepository.getById(input.id) } returns null

        // When
        val exception = assertFailsWith<ResourceNotFoundException> {
            runBlocking {
                useCase(input)
            }
        }

        // Then
        assertEquals(
            "Package 'PKG-404' was not found",
            exception.message
        )

        coVerify(exactly = 0) {
            packageRepository.update(any())
        }
    }

    @Test
    fun `throws validation exception when update input is invalid`() {
        // Given
        val input = PackageUpdateInput(
            id = "PKG-001"
        )

        every {
            validator(input)
        } returns ValidatorResult.Invalid(
            listOf(
                ValidatorError.NoFieldsProvided(ValidatorField.ENTITY)
            )
        )

        // When
        val exception = assertFailsWith<EntityValidationException> {
            runBlocking {
                useCase(input)
            }
        }

        // Then
        assertEquals(
            ValidatorField.ENTITY,
            exception.violations.single().field
        )

        coVerify(exactly = 0) {
            packageRepository.getById(any())
        }

        coVerify(exactly = 0) {
            packageRepository.update(any())
        }
    }
}