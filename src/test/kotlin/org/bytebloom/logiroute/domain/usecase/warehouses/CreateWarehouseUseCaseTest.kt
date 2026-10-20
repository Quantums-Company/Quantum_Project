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
import org.bytebloom.logiroute.domain.repository.WarehouseRepository
import org.bytebloom.logiroute.domain.service.IdGenerator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class CreateWarehouseUseCaseTest {

    private val repository: WarehouseRepository = mockk()
    private val idGenerator: IdGenerator = mockk()
    private lateinit var useCase: CreateWarehouseUseCase

    private val generatedId = "WH-001"
    private val validName = "Main Hub"
    private val validRegionalZone = "Central"
    private val validLongitude = 35.2
    private val validLatitude = 31.9

    @BeforeEach
    fun setUp() {
        every { idGenerator.next(EntityType.WAREHOUSE) } returns generatedId
        coEvery { repository.create(any()) } answers { firstArg() }
        useCase = CreateWarehouseUseCase(repository, idGenerator)
    }

    @Test
    fun `should create warehouse with generated id when data is valid`() = runTest {
        // When
        val result = useCase(validName, validRegionalZone, validLongitude, validLatitude)

        // Then
        assertThat(result.id).isEqualTo(generatedId)
    }

    @Test
    fun `should create warehouse with provided details when data is valid`() = runTest {
        // When
        val result = useCase(validName, validRegionalZone, validLongitude, validLatitude)

        // Then
        assertThat(result.name).isEqualTo(validName)
        assertThat(result.regionalZone).isEqualTo(validRegionalZone)
        assertThat(result.longitude).isEqualTo(validLongitude)
        assertThat(result.latitude).isEqualTo(validLatitude)
    }

    @Test
    fun `should call repository create exactly once when data is valid`() = runTest {
        // When
        useCase(validName, validRegionalZone, validLongitude, validLatitude)

        // Then
        coVerify(exactly = 1) { repository.create(any()) }
    }

    @Test
    fun `should throw validation exception when warehouse name is blank`() = runTest {
        // Given
        val blankName = "    "

        // When
        val action = suspend { useCase(blankName, validRegionalZone, validLongitude, validLatitude) }

        // Then
        assertFailsWith<EntityValidationException> { action() }
    }

    @Test
    fun `should throw validation exception when warehouse name is empty`() = runTest {
        // Given
        val emptyName = ""

        // When
        val action = suspend { useCase(emptyName, validRegionalZone, validLongitude, validLatitude) }

        // Then
        assertFailsWith<EntityValidationException> { action() }
    }

    @Test
    fun `should not call repository when warehouse name is empty`() = runTest {
        // Given
        val emptyName = ""

        // When
        runCatching { useCase(emptyName, validRegionalZone, validLongitude, validLatitude) }

        // Then
        coVerify(exactly = 0) { repository.create(any()) }
    }

    @Test
    fun `should throw validation exception when latitude is out of geographic range`() = runTest {
        // Given
        val outOfRangeLatitude = 95.0

        // When
        val action = suspend { useCase(validName, validRegionalZone, validLongitude, outOfRangeLatitude) }

        // Then
        assertFailsWith<EntityValidationException> { action() }
    }

    @Test
    fun `should throw validation exception when longitude is out of geographic range`() = runTest {
        // Given
        val outOfRangeLongitude = 190.0

        // When
        val action = suspend { useCase(validName, validRegionalZone, outOfRangeLongitude, validLatitude) }

        // Then
        assertFailsWith<EntityValidationException> { action() }
    }

    @Test
    fun `should not call repository when coordinates are out of geographic range`() = runTest {
        // Given
        val outOfRangeLongitude = 190.0

        // When
        runCatching { useCase(validName, validRegionalZone, outOfRangeLongitude, validLatitude) }

        // Then
        coVerify(exactly = 0) { repository.create(any()) }
    }

    @Test
    fun `should propagate repository exception when saving fails`() = runTest {
        // Given
        val conflictMessage = "Warehouse already exists"
        coEvery { repository.create(any()) } throws DatabaseConflictException(conflictMessage)

        // When
        val action = suspend { useCase(validName, validRegionalZone, validLongitude, validLatitude) }

        // Then
        val exception = assertFailsWith<DatabaseConflictException> { action() }
        assertThat(exception).hasMessageThat().isEqualTo(conflictMessage)
    }
}
