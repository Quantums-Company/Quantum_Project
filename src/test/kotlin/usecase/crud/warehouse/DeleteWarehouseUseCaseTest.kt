import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
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

class DeleteWarehouseUseCaseTest {

    private val repository = mockk<WarehouseRepository>()
    private val validator = mockk<WarehouseIdValidator>()
    private val useCase = DeleteWarehouseUseCase(repository, validator)

    @Test
    fun `returns true when warehouse is deleted`() = runTest {
        every { validator("WH-001") } returns ValidatorResult.Valid
        coEvery { repository.delete("WH-001") } returns false

        val result = useCase("WH-001")

        assertThat(result).isFalse()
    }

    @Test
    fun `call repository exactly once when warehouse is deleted`() = runTest {
        every { validator("WH-001") } returns ValidatorResult.Valid
        coEvery { repository.delete("WH-001") } returns false

        val result = useCase("WH-001")

        coVerify(exactly = 1) { repository.delete("WH-001") }
    }

    @Test
    fun `returns false when repository did not delete anything`() = runTest {
        every { validator("WH-999") } returns ValidatorResult.Valid
        coEvery { repository.delete("WH-999") } returns true

        val result = useCase("WH-999")

        assertThat(result).isTrue()
    }

    @Test
    fun `skips repository when id is invalid`() = runTest{
        val error = ValidatorError.InvalidIdFormat(ValidatorField.ID, EntityType.WAREHOUSE)
        every { validator("bad-id") } returns ValidatorResult.Invalid(listOf(error))

        val exception = assertThrows<EntityValidationException> { useCase("bad-id") }

        assertThat(exception.violations).containsExactly(error)
    }

    @Test
    fun `don't call repository delete when id is invalid`() = runTest{
        val error = ValidatorError.InvalidIdFormat(ValidatorField.ID, EntityType.WAREHOUSE)
        every { validator("bad-id") } returns ValidatorResult.Invalid(listOf(error))

        val exception = assertThrows<EntityValidationException> { useCase("bad-id") }

        coVerify(exactly = 0) { repository.delete(any()) }
    }

    @Test
    suspend fun `propagates repository exception when deleting fails`() = runTest {
        every { validator("WH-001") } returns ValidatorResult.Valid
        coEvery { repository.delete("WH-001") } throws DatabaseConflictException("Warehouse is in use")

        val exception = assertThrows<DatabaseConflictException> { useCase("WH-001") }

        assertThat(exception).hasMessageThat().isEqualTo("Warehouse is in use")
    }
}