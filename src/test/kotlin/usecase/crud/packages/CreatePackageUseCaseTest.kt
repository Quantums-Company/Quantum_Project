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
import org.bytebloom.domain.service.IdGenerator
import org.bytebloom.domain.usecase.crud.packages.CreatePackageUseCase
import org.bytebloom.domain.validator.create.CreatePackageValidator
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class CreatePackageUseCaseTest {

    private val packageRepository = mockk<PackageRepository>()
    private val validator = mockk<CreatePackageValidator>()
    private val idGenerator = mockk<IdGenerator>()

    private fun warehouse(id: String): Warehouse =
        Warehouse(
            id = id,
            name = "Test Warehouse",
            regionalZone = "CENTRAL",
            longitude = 35.0,
            latitude = 32.0
        )

    private val useCase = CreatePackageUseCase(
        packageRepository = packageRepository,
        validator = validator,
        idGenerator = idGenerator
    )

    @Test
    fun `creates a package and stores it in the repository`() {
        // Given
        val origin = warehouse("WH-001")
        val destination = warehouse("WH-002")
        val storedPackage = Package(
            id = "PKG-001",
            weight = 10.0,
            priority = Priority.STANDARD,
            originWarehouse = origin,
            destinationWarehouse = destination
        )

        every { idGenerator.next(EntityType.PACKAGE) } returns "PKG-001"
        every { validator(any()) } returns ValidatorResult.Valid
        coEvery { packageRepository.create(any()) } returns storedPackage

        // When
        val result = runBlocking {
            useCase(
                weight = 10.0,
                priority = Priority.STANDARD,
                originWarehouse = origin,
                destinationWarehouse = destination
            )
        }

        // Then
        assertSame(storedPackage, result)
        assertEquals("PKG-001", result.id)
        assertEquals(10.0, result.weight)

        verify(exactly = 1) {
            idGenerator.next(EntityType.PACKAGE)
        }

        coVerify(exactly = 1) {
            packageRepository.create(
                match {
                    it.id == "PKG-001" &&
                            it.weight == 10.0 &&
                            it.priority == Priority.STANDARD
                }
            )
        }
    }

    @Test
    fun `throws validation exception when validator rejects the package`() {
        // Given
        val origin = warehouse("WH-001")
        val destination = warehouse("WH-002")

        every { idGenerator.next(EntityType.PACKAGE) } returns "PKG-001"
        every {
            validator(any())
        } returns ValidatorResult.Invalid(
            listOf(ValidatorError.NotPositive(ValidatorField.WEIGHT))
        )

        // When
        val exception = assertFailsWith<EntityValidationException> {
            runBlocking {
                useCase(
                    weight = 10.0,
                    priority = Priority.STANDARD,
                    originWarehouse = origin,
                    destinationWarehouse = destination
                )
            }
        }
        // Then
        assertEquals(
            ValidatorField.WEIGHT,
            exception.violations.single().field
        )

        coVerify(exactly = 0) {
            packageRepository.create(any())
        }
    }
}