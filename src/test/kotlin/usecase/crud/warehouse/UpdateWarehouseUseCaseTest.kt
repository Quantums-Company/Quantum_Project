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
    fun `updates only the provided fields and keeps the id`() = runTest {
        val input = WarehouseUpdateInput(id = "WH-001", name = "New Name", latitude = 32.5)
        val savedWarehouse = slot<Warehouse>()
        every { validator(input) } returns ValidatorResult.Valid
        coEvery { repository.getById("WH-001") } returns existing
        coEvery { repository.update(capture(savedWarehouse)) } answers { savedWarehouse.captured }

        val result = useCase(input)

        assertThat(result.id).isEqualTo("WH-001")
    }

    @Test
    fun `updates only the provided fields and keeps the name`() = runTest {
        val input = WarehouseUpdateInput(id = "WH-001", name = "New Name", latitude = 32.5)
        val savedWarehouse = slot<Warehouse>()
        every { validator(input) } returns ValidatorResult.Valid
        coEvery { repository.getById("WH-001") } returns existing
        coEvery { repository.update(capture(savedWarehouse)) } answers { savedWarehouse.captured }

        val result = useCase(input)

        assertThat(result.name).isEqualTo("New Name")
    }

    @Test
    fun `updates only the provided fields and keeps the latitude`() = runTest {
        val input = WarehouseUpdateInput(id = "WH-001", name = "New Name", latitude = 32.5)
        val savedWarehouse = slot<Warehouse>()
        every { validator(input) } returns ValidatorResult.Valid
        coEvery { repository.getById("WH-001") } returns existing
        coEvery { repository.update(capture(savedWarehouse)) } answers { savedWarehouse.captured }

        val result = useCase(input)

        assertThat(result.latitude).isEqualTo(32.5)
    }

    @Test
    fun `updates only the provided fields and keeps the regionalZone`() = runTest {
        val input = WarehouseUpdateInput(id = "WH-001", name = "New Name", latitude = 32.5)
        val savedWarehouse = slot<Warehouse>()
        every { validator(input) } returns ValidatorResult.Valid
        coEvery { repository.getById("WH-001") } returns existing
        coEvery { repository.update(capture(savedWarehouse)) } answers { savedWarehouse.captured }

        val result = useCase(input)

        assertThat(result.regionalZone).isEqualTo("North")
    }

    @Test
    fun `updates only the provided fields and keeps the longitude`() = runTest {
        val input = WarehouseUpdateInput(id = "WH-001", name = "New Name", latitude = 32.5)
        val savedWarehouse = slot<Warehouse>()
        every { validator(input) } returns ValidatorResult.Valid
        coEvery { repository.getById("WH-001") } returns existing
        coEvery { repository.update(capture(savedWarehouse)) } answers { savedWarehouse.captured }

        val result = useCase(input)

        assertThat(result.longitude).isEqualTo(35.0)
    }

    @Test
    fun `call repository update exactly once`() = runTest {
        val input = WarehouseUpdateInput(id = "WH-001", name = "New Name", latitude = 32.5)
        val savedWarehouse = slot<Warehouse>()
        every { validator(input) } returns ValidatorResult.Valid
        coEvery { repository.getById("WH-001") } returns existing
        coEvery { repository.update(capture(savedWarehouse)) } answers { savedWarehouse.captured }

        useCase(input)

        coVerify(exactly = 1) { repository.update(any()) }
    }

    @Test
    suspend fun `throws ResourceNotFoundException when warehouse does not exist`() {
        val input = WarehouseUpdateInput(id = "WH-999", name = "New Name")
        every { validator(input) } returns ValidatorResult.Valid
        coEvery { repository.getById("WH-999") } returns null

        val exception = assertThrows<ResourceNotFoundException> { useCase(input) }

        assertThat(exception).hasMessageThat().contains("WH-999")
    }

    @Test
    fun `don't call repository update`() {
        val input = WarehouseUpdateInput(id = "WH-999", name = "New Name")
        every { validator(input) } returns ValidatorResult.Valid
        coEvery { repository.getById("WH-999") } returns null

        coVerify(exactly = 0) { repository.update(any()) }
    }

    @Test
    suspend fun `throws EntityValidationException and skips repository when input is invalid`() {
        val input = WarehouseUpdateInput(id = "WH-001")
        val error = ValidatorError.NoFieldsProvided(ValidatorField.ENTITY)
        every { validator(input) } returns ValidatorResult.Invalid(listOf(error))

        val exception = assertThrows<EntityValidationException> { useCase(input) }

        assertThat(exception.violations).containsExactly(error)
        coVerify(exactly = 0) { repository.getById(any()) }
        coVerify(exactly = 0) { repository.update(any()) }
    }

    @Test
    suspend fun `skips repository when input is invalid`() {
        val input = WarehouseUpdateInput(id = "WH-001")
        val error = ValidatorError.NoFieldsProvided(ValidatorField.ENTITY)
        every { validator(input) } returns ValidatorResult.Invalid(listOf(error))

        val exception = assertThrows<EntityValidationException> { useCase(input) }

        assertThat(exception.violations).containsExactly(error)
    }

    @Test
    fun `don't call repository update when id is invalid`() {
        val input = WarehouseUpdateInput(id = "WH-001")
        val error = ValidatorError.NoFieldsProvided(ValidatorField.ENTITY)
        every { validator(input) } returns ValidatorResult.Invalid(listOf(error))

        coVerify(exactly = 0) { repository.getById(any()) }
    }

    @Test
    fun `don't call repository update when id in valid`() {
        val input = WarehouseUpdateInput(id = "WH-001")
        val error = ValidatorError.NoFieldsProvided(ValidatorField.ENTITY)
        every { validator(input) } returns ValidatorResult.Invalid(listOf(error))

        coVerify(exactly = 0) { repository.update(any()) }
    }

    @Test
    suspend fun `propagates repository exception when saving fails`() {
        val input = WarehouseUpdateInput(id = "WH-001", name = "New Name")
        every { validator(input) } returns ValidatorResult.Valid
        coEvery { repository.getById("WH-001") } returns existing
        coEvery { repository.update(any()) } throws DatabaseConflictException("Name already used")

        val exception = assertThrows<DatabaseConflictException> { useCase(input) }

        assertThat(exception).hasMessageThat().isEqualTo("Name already used")
    }
}