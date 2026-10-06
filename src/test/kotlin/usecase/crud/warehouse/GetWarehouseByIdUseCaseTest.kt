package usecase.crud.warehouse

import org.junit.jupiter.api.Test
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.exception.NetworkUnavailableException
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.domain.usecase.crud.warehouse.GetWarehouseByIdUseCase
import org.bytebloom.domain.validator.id.WarehouseIdValidator
import org.junit.jupiter.api.assertThrows

class GetWarehouseByIdUseCaseTest {

    private val repository = mockk<WarehouseRepository>()
    private val validator = mockk<WarehouseIdValidator>()
    private val useCase = GetWarehouseByIdUseCase(repository, validator)
    private val warehouse = Warehouse("WH-001", "Main Hub", "Central", 35.2, 31.9)

    @Test
    fun `returns warehouse when id is valid and warehouse exists`() = runTest {
        every { validator("WH-001") } returns ValidatorResult.Valid
        coEvery { repository.getById("WH-001") } returns warehouse

        val result = useCase("WH-001")

        assertThat(result).isEqualTo(warehouse)
    }

    @Test
    fun `returns null when id is valid but warehouse does not exist`() = runTest {
        every { validator("WH-999") } returns ValidatorResult.Valid
        coEvery { repository.getById("WH-999") } returns null

        val result = useCase("WH-999")

        assertThat(result).isNull()
    }

    @Test
    fun `skips repository when id is invalid`() = runTest {
        val error = ValidatorError.InvalidIdFormat(ValidatorField.ID, EntityType.WAREHOUSE)
        every { validator("bad-id") } returns ValidatorResult.Invalid(listOf(error))

        val exception = assertThrows<EntityValidationException> { useCase("bad-id") }

        assertThat(exception.violations).containsExactly(error)
    }

    @Test
    fun `don't call repository get by id when id is invalid`() = runTest {
        val error = ValidatorError.InvalidIdFormat(ValidatorField.ID, EntityType.WAREHOUSE)
        every { validator("bad-id") } returns ValidatorResult.Invalid(listOf(error))

        coVerify(exactly = 0) { repository.getById(any()) }
    }

    @Test
    fun `propagates repository exception when fetching fails`() = runTest {
        every { validator("WH-001") } returns ValidatorResult.Valid
        coEvery { repository.getById("WH-001") } throws NetworkUnavailableException()

        assertThrows<NetworkUnavailableException> { useCase("WH-001") }
    }
}