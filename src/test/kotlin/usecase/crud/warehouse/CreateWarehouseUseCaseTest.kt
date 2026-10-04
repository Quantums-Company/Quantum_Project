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
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.domain.service.IdGenerator
import org.bytebloom.domain.usecase.crud.warehouse.CreateWarehouseUseCase
import org.bytebloom.domain.validator.create.CreateWarehouseValidator
import org.junit.jupiter.api.assertThrows

class CreateWarehouseUseCaseTest {
    private val repository = mockk<WarehouseRepository>()
    private val validator = mockk<CreateWarehouseValidator>()
    private val idGenerator = mockk<IdGenerator>()
    private val useCase = CreateWarehouseUseCase(repository, validator, idGenerator)

    @Test
    fun `creates warehouse with generated id when data is valid`() = runTest {
        // Given
        val savedWarehouse = slot<Warehouse>()
        every { idGenerator.next(EntityType.WAREHOUSE) } returns "WH-001"
        every { validator(any()) } returns ValidatorResult.Valid
        coEvery { repository.create(capture(savedWarehouse)) } answers { savedWarehouse.captured }

        // When
        val result = useCase("Main Hub", "Central", 35.2, 31.9)

        // Then
        assertThat(result.id).isEqualTo("WH-001")
        assertThat(result.name).isEqualTo("Main Hub")
        assertThat(result.regionalZone).isEqualTo("Central")
        assertThat(result.longitude).isEqualTo(35.2)
        assertThat(result.latitude).isEqualTo(31.9)
        coVerify(exactly = 1) { repository.create(any()) }
    }

    @Test
    suspend fun `throws EntityValidationException and does not save when validator returns Invalid`() {
        // Given
        val error = ValidatorError.Blank(ValidatorField.NAME)
        every { idGenerator.next(EntityType.WAREHOUSE) } returns "WH-001"
        every { validator(any()) } returns ValidatorResult.Invalid(listOf(error))

        // When
        val exception = assertThrows<EntityValidationException> { useCase("Main Hub", "Central", 35.2, 31.9) }

        // Then
        assertThat(exception.violations).containsExactly(error)
        coVerify(exactly = 0) { repository.create(any()) }
    }

    @Test
    suspend fun `throws EntityValidationException when name is blank`() {
        // Given
        every { idGenerator.next(EntityType.WAREHOUSE) } returns "WH-001"

        // When
        val exception = assertThrows<EntityValidationException> { useCase("   ", "Central", 35.2, 31.9) }

        // Then
        assertThat(exception.violations.map { it.field }).containsExactly(ValidatorField.NAME)
        coVerify(exactly = 0) { repository.create(any()) }
    }

    @Test
    suspend fun `throws EntityValidationException when latitude is out of range`() {
        // Given
        every { idGenerator.next(EntityType.WAREHOUSE) } returns "WH-001"

        // When
        val exception = assertThrows<EntityValidationException> {useCase("Main Hub", "Central", 35.2, 95.0) }

        // Then
        assertThat(exception.violations.map { it.field }).containsExactly(ValidatorField.LATITUDE)
        coVerify(exactly = 0) { repository.create(any()) }
    }

    @Test
    suspend fun `propagates repository exception when saving fails`() {
        // Given
        every { idGenerator.next(EntityType.WAREHOUSE) } returns "WH-001"
        every { validator(any()) } returns ValidatorResult.Valid
        coEvery { repository.create(any()) } throws DatabaseConflictException("Warehouse already exists")

        // When
        val exception = assertThrows<DatabaseConflictException> {useCase("Main Hub", "Central", 35.2, 31.9) }

        // Then
        assertThat(exception).hasMessageThat().isEqualTo("Warehouse already exists")
    }
}
