package usecase.crud.warehouse

import org.junit.jupiter.api.Test
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.exception.DatabaseConflictException
import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.exception.ResourceNotFoundException
import org.bytebloom.domain.model.input.update.WarehouseUpdateInput
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.domain.usecase.crud.warehouse.UpdateWarehouseUseCase
import org.bytebloom.domain.validator.update.UpdateWarehouseValidator
import org.junit.jupiter.api.assertThrows


class UpdateWarehouseUseCaseTest {
    private val repository = mockk<WarehouseRepository>()
    private val validator = mockk<UpdateWarehouseValidator>()
    private val useCase = UpdateWarehouseUseCase(repository, validator)
    private val existing = Warehouse("WH-001", "Old Name", "North", 35.0, 31.0)

    @Test
    fun `updates only the provided fields and keeps the rest`() = runTest {
        // Given
        val input = WarehouseUpdateInput(id = "WH-001", name = "New Name", latitude = 32.5)
        val savedWarehouse = slot<Warehouse>()
        every { validator(input) } returns ValidatorResult.Valid
        coEvery { repository.getById("WH-001") } returns existing
        coEvery { repository.update(capture(savedWarehouse)) } answers { savedWarehouse.captured }

        // When
        val result = useCase(input)

        // Then
        assertThat(result.id).isEqualTo("WH-001")
        assertThat(result.name).isEqualTo("New Name")
        assertThat(result.latitude).isEqualTo(32.5)
        assertThat(result.regionalZone).isEqualTo("North")
        assertThat(result.longitude).isEqualTo(35.0)
        coVerify(exactly = 1) { repository.update(any()) }
    }

    @Test
    suspend fun `throws ResourceNotFoundException when warehouse does not exist`() {
        // Given
        val input = WarehouseUpdateInput(id = "WH-999", name = "New Name")
        every { validator(input) } returns ValidatorResult.Valid
        coEvery { repository.getById("WH-999") } returns null

        // When
        val exception = assertThrows<ResourceNotFoundException> { useCase(input) }

        // Then
        assertThat(exception).hasMessageThat().contains("WH-999")
        coVerify(exactly = 0) { repository.update(any()) }
    }

    @Test
    suspend fun `throws EntityValidationException and skips repository when input is invalid`() {
        // Given
        val input = WarehouseUpdateInput(id = "WH-001")
        val error = ValidatorError.NoFieldsProvided(ValidatorField.ENTITY)
        every { validator(input) } returns ValidatorResult.Invalid(listOf(error))

        // When
        val exception = assertThrows<EntityValidationException> { useCase(input) }

        // Then
        assertThat(exception.violations).containsExactly(error)
        coVerify(exactly = 0) { repository.getById(any()) }
        coVerify(exactly = 0) { repository.update(any()) }
    }

    @Test
    suspend fun `propagates repository exception when saving fails`() {
        // Given
        val input = WarehouseUpdateInput(id = "WH-001", name = "New Name")
        every { validator(input) } returns ValidatorResult.Valid
        coEvery { repository.getById("WH-001") } returns existing
        coEvery { repository.update(any()) } throws DatabaseConflictException("Name already used")

        // When
        val exception = assertThrows<DatabaseConflictException> { useCase(input) }

        // Then
        assertThat(exception).hasMessageThat().isEqualTo("Name already used")
    }
}