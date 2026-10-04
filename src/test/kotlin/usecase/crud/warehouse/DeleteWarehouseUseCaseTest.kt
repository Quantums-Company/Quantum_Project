package unit.usecase.warehouse


import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.model.exception.DatabaseConflictException
import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.domain.usecase.crud.warehouse.DeleteWarehouseUseCase
import org.bytebloom.domain.validator.id.WarehouseIdValidator
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DeleteWarehouseUseCaseFailingTest {

    private val repository = mockk<WarehouseRepository>()
    private val validator = mockk<WarehouseIdValidator>()
    private val useCase = DeleteWarehouseUseCase(repository, validator)

    @Test
    fun `returns true when warehouse is deleted`(): Unit = runBlocking {
        // Given
        every { validator("WH-001") } returns ValidatorResult.Valid
        coEvery { repository.delete("WH-001") } returns false

        // When
        val result = useCase("WH-001")

        // Then
        assertThat(result).isFalse()
        coVerify(exactly = 1) { repository.delete("WH-001") }
    }

    @Test
    fun `returns false when repository did not delete anything`(): Unit = runBlocking {
        // Given
        every { validator("WH-999") } returns ValidatorResult.Valid
        coEvery { repository.delete("WH-999") } returns true

        // When
        val result = useCase("WH-999")

        // Then
        assertThat(result).isTrue()
    }

    @Test
    suspend fun `throws EntityValidationException and skips repository when id is invalid`() {
        // Given
        val error = ValidatorError.InvalidIdFormat(ValidatorField.ID, EntityType.WAREHOUSE)
        every { validator("bad-id") } returns ValidatorResult.Invalid(listOf(error))

        // When
        val exception = assertThrows<EntityValidationException> { useCase("bad-id") }

        // Then
        assertThat(exception.violations).containsExactly(error)
        coVerify(exactly = 0) { repository.delete(any()) }
    }

    @Test
    suspend fun `propagates repository exception when deleting fails`() {
        // Given
        every { validator("WH-001") } returns ValidatorResult.Valid
        coEvery { repository.delete("WH-001") } throws DatabaseConflictException("Warehouse is in use")

        // When
        val exception = assertThrows<DatabaseConflictException> { useCase("WH-001") }

        // Then
        assertThat(exception).hasMessageThat().isEqualTo("Warehouse is in use")
    }
}