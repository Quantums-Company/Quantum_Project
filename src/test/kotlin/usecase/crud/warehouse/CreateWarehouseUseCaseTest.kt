package usecase.crud.warehouse

import org.junit.jupiter.api.Test
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.exception.DatabaseConflictException
import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.domain.service.IdGenerator
import org.bytebloom.domain.usecase.crud.warehouse.CreateWarehouseUseCase
import org.junit.jupiter.api.assertThrows

class CreateWarehouseUseCaseTest {

    private val repository = mockk<WarehouseRepository>()
    private val idGenerator = mockk<IdGenerator>()
    private val useCase = CreateWarehouseUseCase(repository, idGenerator)

    @Test
    fun `creates warehouse with generated id when data is valid`() = runTest {
        val savedWarehouse = slot<Warehouse>()
        every { idGenerator.next(EntityType.WAREHOUSE) } returns "WH-001"
        coEvery { repository.create(capture(savedWarehouse)) } answers { savedWarehouse.captured }

        val result = useCase("Main Hub", "Central", 35.2, 31.9)

        assertThat(result.id).isEqualTo("WH-001")
        assertThat(result.name).isEqualTo("Main Hub")
        assertThat(result.regionalZone).isEqualTo("Central")
        assertThat(result.longitude).isEqualTo(35.2)
        assertThat(result.latitude).isEqualTo(31.9)
        coVerify(exactly = 1) { repository.create(any()) }
    }

    @Test
    fun `throws EntityValidationException when warehouse name is blank or empty`() = runTest {
        every { idGenerator.next(EntityType.WAREHOUSE) } returns "WH-001"

        assertThrows<EntityValidationException> { useCase("   ", "Central", 35.2, 31.9) }
        assertThrows<EntityValidationException> { useCase("", "Central", 35.2, 31.9) }
        coVerify(exactly = 0) { repository.create(any()) }
    }

    @Test
    fun `throws EntityValidationException when latitude or longitude are out of geographic range`() = runTest {
        every { idGenerator.next(EntityType.WAREHOUSE) } returns "WH-001"

        // Invalid Latitude (> 90)
        assertThrows<EntityValidationException> { useCase("Main Hub", "Central", 35.2, 95.0) }
        // Invalid Longitude (> 180)
        assertThrows<EntityValidationException> { useCase("Main Hub", "Central", 190.0, 31.9) }
        coVerify(exactly = 0) { repository.create(any()) }
    }

    @Test
    fun `propagates repository exception when saving fails`() = runTest {
        every { idGenerator.next(EntityType.WAREHOUSE) } returns "WH-001"
        coEvery { repository.create(any()) } throws DatabaseConflictException("Warehouse already exists")

        val exception = assertThrows<DatabaseConflictException> { useCase("Main Hub", "Central", 35.2, 31.9) }

        assertThat(exception).hasMessageThat().isEqualTo("Warehouse already exists")
    }
}