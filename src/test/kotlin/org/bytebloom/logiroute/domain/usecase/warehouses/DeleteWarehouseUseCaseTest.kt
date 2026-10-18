package org.bytebloom.logiroute.domain.usecase.warehouses

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.model.EntityType
import org.bytebloom.logiroute.domain.model.exception.DatabaseConflictException
import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.model.validation.ValidatorError
import org.bytebloom.logiroute.domain.model.validation.ValidatorField
import org.bytebloom.logiroute.domain.model.validation.ValidatorResult
import org.bytebloom.logiroute.domain.repository.WarehouseRepository
import org.bytebloom.logiroute.domain.validator.id.WarehouseIdValidator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class DeleteWarehouseUseCaseTest {

    private val repository: WarehouseRepository = mockk()
    private val validator: WarehouseIdValidator = mockk()
    private lateinit var useCase: DeleteWarehouseUseCase

    @BeforeEach
    fun setUp() {
        useCase = DeleteWarehouseUseCase(repository, validator)
    }

    @Test
    fun `should return true when warehouse is deleted`() = runTest {
        // Given
        val warehouseId = "WH-001"
        every { validator(warehouseId) } returns ValidatorResult.Valid
        coEvery { repository.delete(warehouseId) } returns true

        // When
        val result = useCase(warehouseId)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `should call repository delete exactly once when warehouse is deleted`() = runTest {
        // Given
        val warehouseId = "WH-001"
        every { validator(warehouseId) } returns ValidatorResult.Valid
        coEvery { repository.delete(warehouseId) } returns true

        // When
        useCase(warehouseId)

        // Then
        coVerify(exactly = 1) { repository.delete(warehouseId) }
    }

    @Test
    fun `should return false when repository did not delete anything`() = runTest {
        // Given
        val unknownWarehouseId = "WH-999"
        every { validator(unknownWarehouseId) } returns ValidatorResult.Valid
        coEvery { repository.delete(unknownWarehouseId) } returns false

        // When
        val result = useCase(unknownWarehouseId)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `should throw validation exception with id violation when id is invalid`() = runTest {
        // Given
        val invalidId = "bad-id"
        val error = createInvalidIdError()
        every { validator(invalidId) } returns ValidatorResult.Invalid(listOf(error))

        // When
        val action = suspend { useCase(invalidId) }

        // Then
        val exception = assertFailsWith<EntityValidationException> { action() }
        assertThat(exception.violations).containsExactly(error)
    }

    @Test
    fun `should not call repository when id is invalid`() = runTest {
        // Given
        val invalidId = "bad-id"
        every { validator(invalidId) } returns ValidatorResult.Invalid(listOf(createInvalidIdError()))

        // When
        runCatching { useCase(invalidId) }

        // Then
        coVerify(exactly = 0) { repository.delete(any()) }
    }

    @Test
    fun `should propagate repository exception when deleting fails`() = runTest {
        // Given
        val warehouseId = "WH-001"
        val conflictMessage = "Warehouse is in use"
        every { validator(warehouseId) } returns ValidatorResult.Valid
        coEvery { repository.delete(warehouseId) } throws DatabaseConflictException(conflictMessage)

        // When
        val action = suspend { useCase(warehouseId) }

        // Then
        val exception = assertFailsWith<DatabaseConflictException> { action() }
        assertThat(exception).hasMessageThat().isEqualTo(conflictMessage)
    }

    private fun createInvalidIdError() = ValidatorError.InvalidIdFormat(
        ValidatorField.ID,
        EntityType.WAREHOUSE
    )
}
