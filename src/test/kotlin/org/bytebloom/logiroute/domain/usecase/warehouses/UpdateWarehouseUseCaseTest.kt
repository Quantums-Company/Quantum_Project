package org.bytebloom.logiroute.domain.usecase.warehouses

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.exception.DatabaseConflictException
import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.model.exception.ResourceNotFoundException
import org.bytebloom.logiroute.domain.model.input.update.WarehouseUpdateInput
import org.bytebloom.logiroute.domain.model.validation.ValidatorError
import org.bytebloom.logiroute.domain.model.validation.ValidatorField
import org.bytebloom.logiroute.domain.model.validation.ValidatorResult
import org.bytebloom.logiroute.domain.repository.WarehouseRepository
import org.bytebloom.logiroute.domain.validator.update.UpdateWarehouseValidator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class UpdateWarehouseUseCaseTest {

    private val repository: WarehouseRepository = mockk()
    private val validator: UpdateWarehouseValidator = mockk()
    private lateinit var useCase: UpdateWarehouseUseCase

    private val existingWarehouse = createWarehouse(
        id = "WH-001",
        name = "Old Name",
        regionalZone = "North",
        longitude = 35.0,
        latitude = 31.0
    )

    @BeforeEach
    fun setUp() {
        useCase = UpdateWarehouseUseCase(repository, validator)
    }

    @Test
    fun `should apply provided fields when update input has only some fields`() = runTest {
        // Given
        val newName = "New Name"
        val newLatitude = 32.5
        val input = createUpdateInput(name = newName, latitude = newLatitude)
        every { validator(input) } returns ValidatorResult.Valid
        coEvery { repository.getById(existingWarehouse.id) } returns existingWarehouse
        coEvery { repository.update(any()) } answers { firstArg() }

        // When
        val result = useCase(input)

        // Then
        assertThat(result.name).isEqualTo(newName)
        assertThat(result.latitude).isEqualTo(newLatitude)
    }

    @Test
    fun `should keep existing fields when they are not provided in update input`() = runTest {
        // Given
        val input = createUpdateInput(name = "New Name", latitude = 32.5)
        every { validator(input) } returns ValidatorResult.Valid
        coEvery { repository.getById(existingWarehouse.id) } returns existingWarehouse
        coEvery { repository.update(any()) } answers { firstArg() }

        // When
        val result = useCase(input)

        // Then
        assertThat(result.id).isEqualTo(existingWarehouse.id)
        assertThat(result.regionalZone).isEqualTo(existingWarehouse.regionalZone)
        assertThat(result.longitude).isEqualTo(existingWarehouse.longitude)
    }

    @Test
    fun `should call repository update exactly once when input is valid`() = runTest {
        // Given
        val input = createUpdateInput(name = "New Name")
        every { validator(input) } returns ValidatorResult.Valid
        coEvery { repository.getById(existingWarehouse.id) } returns existingWarehouse
        coEvery { repository.update(any()) } answers { firstArg() }

        // When
        useCase(input)

        // Then
        coVerify(exactly = 1) { repository.update(any()) }
    }

    @Test
    fun `should throw resource not found exception with warehouse id when warehouse does not exist`() = runTest {
        // Given
        val unknownWarehouseId = "WH-999"
        val input = createUpdateInput(id = unknownWarehouseId, name = "New Name")
        every { validator(input) } returns ValidatorResult.Valid
        coEvery { repository.getById(unknownWarehouseId) } returns null

        // When
        val action = suspend { useCase(input) }

        // Then
        val exception = assertFailsWith<ResourceNotFoundException> { action() }
        assertThat(exception).hasMessageThat().contains(unknownWarehouseId)
    }

    @Test
    fun `should not call repository update when warehouse does not exist`() = runTest {
        // Given
        val unknownWarehouseId = "WH-999"
        val input = createUpdateInput(id = unknownWarehouseId, name = "New Name")
        every { validator(input) } returns ValidatorResult.Valid
        coEvery { repository.getById(unknownWarehouseId) } returns null

        // When
        runCatching { useCase(input) }

        // Then
        coVerify(exactly = 0) { repository.update(any()) }
    }

    @Test
    fun `should throw validation exception with violations when input is invalid`() = runTest {
        // Given
        val input = createUpdateInput()
        val error = ValidatorError.NoFieldsProvided(ValidatorField.ENTITY)
        every { validator(input) } returns ValidatorResult.Invalid(listOf(error))

        // When
        val action = suspend { useCase(input) }

        // Then
        val exception = assertFailsWith<EntityValidationException> { action() }
        assertThat(exception.violations).containsExactly(error)
    }

    @Test
    fun `should not call repository when input is invalid`() = runTest {
        // Given
        val input = createUpdateInput()
        val error = ValidatorError.NoFieldsProvided(ValidatorField.ENTITY)
        every { validator(input) } returns ValidatorResult.Invalid(listOf(error))

        // When
        runCatching { useCase(input) }

        // Then
        coVerify(exactly = 0) { repository.getById(any()) }
        coVerify(exactly = 0) { repository.update(any()) }
    }

    @Test
    fun `should propagate repository exception when saving fails`() = runTest {
        // Given
        val conflictMessage = "Name already used"
        val input = createUpdateInput(name = "New Name")
        every { validator(input) } returns ValidatorResult.Valid
        coEvery { repository.getById(existingWarehouse.id) } returns existingWarehouse
        coEvery { repository.update(any()) } throws DatabaseConflictException(conflictMessage)

        // When
        val action = suspend { useCase(input) }

        // Then
        val exception = assertFailsWith<DatabaseConflictException> { action() }
        assertThat(exception).hasMessageThat().isEqualTo(conflictMessage)
    }

    private fun createWarehouse(
        id: String = "WH-001",
        name: String = "Main Hub",
        regionalZone: String = "North",
        longitude: Double = 35.0,
        latitude: Double = 31.0
    ) = Warehouse(
        id = id,
        name = name,
        regionalZone = regionalZone,
        longitude = longitude,
        latitude = latitude
    )

    private fun createUpdateInput(
        id: String = "WH-001",
        name: String? = null,
        regionalZone: String? = null,
        longitude: Double? = null,
        latitude: Double? = null
    ) = WarehouseUpdateInput(
        id = id,
        name = name,
        regionalZone = regionalZone,
        longitude = longitude,
        latitude = latitude
    )
}
