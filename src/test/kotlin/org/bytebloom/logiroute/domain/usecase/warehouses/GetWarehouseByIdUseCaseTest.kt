package org.bytebloom.logiroute.domain.usecase.warehouses

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.model.EntityType
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.model.exception.NetworkUnavailableException
import org.bytebloom.logiroute.domain.model.validation.ValidatorError
import org.bytebloom.logiroute.domain.model.validation.ValidatorField
import org.bytebloom.logiroute.domain.model.validation.ValidatorResult
import org.bytebloom.logiroute.domain.repository.WarehouseRepository
import org.bytebloom.logiroute.domain.validator.id.WarehouseIdValidator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class GetWarehouseByIdUseCaseTest {

    private val repository: WarehouseRepository = mockk()
    private val validator: WarehouseIdValidator = mockk()
    private lateinit var useCase: GetWarehouseByIdUseCase

    @BeforeEach
    fun setUp() {
        useCase = GetWarehouseByIdUseCase(repository, validator)
    }

    @Test
    fun `should return warehouse when id is valid and warehouse exists`() = runTest {
        // Given
        val warehouse = createWarehouse(id = "WH-001")
        every { validator(warehouse.id) } returns ValidatorResult.Valid
        coEvery { repository.getById(warehouse.id) } returns warehouse

        // When
        val result = useCase(warehouse.id)

        // Then
        assertThat(result).isEqualTo(warehouse)
    }

    @Test
    fun `should return null when id is valid but warehouse does not exist`() = runTest {
        // Given
        val unknownWarehouseId = "WH-999"
        every { validator(unknownWarehouseId) } returns ValidatorResult.Valid
        coEvery { repository.getById(unknownWarehouseId) } returns null

        // When
        val result = useCase(unknownWarehouseId)

        // Then
        assertThat(result).isNull()
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
        coVerify(exactly = 0) { repository.getById(any()) }
    }

    @Test
    fun `should propagate repository exception when fetching fails`() = runTest {
        // Given
        val warehouseId = "WH-001"
        every { validator(warehouseId) } returns ValidatorResult.Valid
        coEvery { repository.getById(warehouseId) } throws NetworkUnavailableException()

        // When
        val action = suspend { useCase(warehouseId) }

        // Then
        assertFailsWith<NetworkUnavailableException> { action() }
    }

    private fun createWarehouse(
        id: String = "WH-001",
        name: String = "Main Hub",
        regionalZone: String = "Central",
        longitude: Double = 35.2,
        latitude: Double = 31.9
    ) = Warehouse(
        id = id,
        name = name,
        regionalZone = regionalZone,
        longitude = longitude,
        latitude = latitude
    )

    private fun createInvalidIdError() = ValidatorError.InvalidIdFormat(
        ValidatorField.ID,
        EntityType.WAREHOUSE
    )
}
