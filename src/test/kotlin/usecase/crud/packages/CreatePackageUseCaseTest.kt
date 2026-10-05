package usecase.crud.packages

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Priority
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.service.IdGenerator
import org.bytebloom.domain.usecase.crud.packages.CreatePackageUseCase
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class CreatePackageUseCaseTest {

    private val packageRepository = mockk<PackageRepository>()
    private val idGenerator = mockk<IdGenerator>()

    private fun warehouse(id: String, name: String = "Test Warehouse"): Warehouse =
        Warehouse(
            id = id,
            name = name,
            regionalZone = "CENTRAL",
            longitude = 35.0,
            latitude = 32.0
        )

    private val useCase = CreatePackageUseCase(
        packageRepository = packageRepository,
        idGenerator = idGenerator
    )

    @Test
    fun `successfully creates package and stores in repository when inputs are valid`() = runTest {
        // Given
        val origin = warehouse("WH-001", "Amman Hub")
        val destination = warehouse("WH-002", "Irbid Depot")
        val storedPackage = Package(
            id = "PKG-001",
            weight = 12.5,
            priority = Priority.URGENT,
            originWarehouse = origin,
            destinationWarehouse = destination
        )

        every { idGenerator.next(EntityType.PACKAGE) } returns "PKG-001"
        coEvery { packageRepository.create(any()) } returns storedPackage

        // When
        val result = useCase(
            weight = 12.5,
            priority = Priority.URGENT,
            originWarehouse = origin,
            destinationWarehouse = destination
        )

        // Then
        assertSame(storedPackage, result)
        assertEquals("PKG-001", result.id)
        assertEquals(12.5, result.weight)

        verify(exactly = 1) { idGenerator.next(EntityType.PACKAGE) }
        coVerify(exactly = 1) { packageRepository.create(any()) }
    }

    @Test
    fun `throws validation exception when user enters negative package weight`() = runTest {
        // Given
        val origin = warehouse("WH-001")
        val destination = warehouse("WH-002")

        every { idGenerator.next(EntityType.PACKAGE) } returns "PKG-001"

        // When & Then (User mistake: negative weight)
        assertFailsWith<EntityValidationException> {
            useCase(
                weight = -5.0,
                priority = Priority.STANDARD,
                originWarehouse = origin,
                destinationWarehouse = destination
            )
        }

        coVerify(exactly = 0) { packageRepository.create(any()) }
    }

    @Test
    fun `throws validation exception when origin and destination warehouses are identical`() = runTest {
        // Given
        val sameWarehouse = warehouse("WH-001")

        every { idGenerator.next(EntityType.PACKAGE) } returns "PKG-001"

        // When & Then (User mistake: sending to the same location)
        assertFailsWith<EntityValidationException> {
            useCase(
                weight = 10.0,
                priority = Priority.STANDARD,
                originWarehouse = sameWarehouse,
                destinationWarehouse = sameWarehouse
            )
        }

        coVerify(exactly = 0) { packageRepository.create(any()) }
    }
}