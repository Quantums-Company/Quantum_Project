package org.bytebloom.logiroute.domain.usecase.routes

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.bytebloom.logiroute.domain.model.EntityType
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.model.exception.NetworkUnavailableException
import org.bytebloom.logiroute.domain.repository.RouteRepository
import org.bytebloom.logiroute.domain.service.IdGenerator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class CreateRouteUseCaseTest {

    private val routeRepository: RouteRepository = mockk()
    private val idGenerator: IdGenerator = mockk()
    private lateinit var useCase: CreateRouteUseCase

    private val generatedRouteId = "RT-001"
    private val originWarehouse = createWarehouse(id = "WH-001", name = "Ramallah Hub")
    private val destinationWarehouse = createWarehouse(id = "WH-002", name = "Nablus Depot")
    private val validDistanceKm = 120.0
    private val validTypicalDelayMin = 15

    @BeforeEach
    fun setUp() {
        every { idGenerator.next(EntityType.ROUTE) } returns generatedRouteId
        coEvery { routeRepository.create(any()) } answers { firstArg() }
        useCase = CreateRouteUseCase(routeRepository, idGenerator)
    }

    @Test
    fun `should return created route with generated id when input is valid`() = runTest {
        // When
        val result = useCase(validDistanceKm, validTypicalDelayMin, originWarehouse, destinationWarehouse)

        // Then
        assertThat(result.id).isEqualTo(generatedRouteId)
    }

    @Test
    fun `should return created route with provided details when input is valid`() = runTest {
        // Given
        val distanceKm = 150.5
        val typicalDelayMin = 20

        // When
        val result = useCase(distanceKm, typicalDelayMin, originWarehouse, destinationWarehouse)

        // Then
        assertThat(result.distanceKm).isEqualTo(distanceKm)
        assertThat(result.typicalDelayMin).isEqualTo(typicalDelayMin)
        assertThat(result.originWarehouse).isEqualTo(originWarehouse)
        assertThat(result.destinationWarehouse).isEqualTo(destinationWarehouse)
    }

    @Test
    fun `should save route through repository exactly once when input is valid`() = runTest {
        // Given
        val distanceKm = 100.0

        // When
        useCase(distanceKm, validTypicalDelayMin, originWarehouse, destinationWarehouse)

        // Then
        coVerify(exactly = 1) {
            routeRepository.create(match { it.id == generatedRouteId && it.distanceKm == distanceKm })
        }
    }

    @Test
    fun `should request a new route id from id generator when input is valid`() = runTest {
        // When
        useCase(validDistanceKm, validTypicalDelayMin, originWarehouse, destinationWarehouse)

        // Then
        verify(exactly = 1) { idGenerator.next(EntityType.ROUTE) }
    }

    @Test
    fun `should throw validation exception when distance is zero`() = runTest {
        // Given
        val zeroDistanceKm = 0.0

        // When
        val action = suspend {
            useCase(zeroDistanceKm, validTypicalDelayMin, originWarehouse, destinationWarehouse)
        }

        // Then
        assertFailsWith<EntityValidationException> { action() }
    }

    @Test
    fun `should throw validation exception when distance is negative`() = runTest {
        // Given
        val negativeDistanceKm = -45.0

        // When
        val action = suspend {
            useCase(negativeDistanceKm, validTypicalDelayMin, originWarehouse, destinationWarehouse)
        }

        // Then
        assertFailsWith<EntityValidationException> { action() }
    }

    @Test
    fun `should throw validation exception when typical delay is negative`() = runTest {
        // Given
        val negativeDelayMin = -15

        // When
        val action = suspend {
            useCase(validDistanceKm, negativeDelayMin, originWarehouse, destinationWarehouse)
        }

        // Then
        assertFailsWith<EntityValidationException> { action() }
    }

    @Test
    fun `should throw validation exception when origin and destination are the same warehouse`() = runTest {
        // When
        val action = suspend {
            useCase(validDistanceKm, validTypicalDelayMin, originWarehouse, originWarehouse)
        }

        // Then
        assertFailsWith<EntityValidationException> { action() }
    }

    @Test
    fun `should not save route when input is invalid`() = runTest {
        // Given
        val zeroDistanceKm = 0.0

        // When
        runCatching {
            useCase(zeroDistanceKm, validTypicalDelayMin, originWarehouse, destinationWarehouse)
        }

        // Then
        coVerify(exactly = 0) { routeRepository.create(any()) }
    }

    @Test
    fun `should propagate network exception when repository is unreachable`() = runTest {
        // Given
        coEvery { routeRepository.create(any()) } throws NetworkUnavailableException()

        // When
        val action = suspend {
            useCase(validDistanceKm, validTypicalDelayMin, originWarehouse, destinationWarehouse)
        }

        // Then
        assertFailsWith<NetworkUnavailableException> { action() }
    }

    private fun createWarehouse(
        id: String = "WH-001",
        name: String = "Ramallah Hub",
        regionalZone: String = "West Bank",
        longitude: Double = 35.2,
        latitude: Double = 31.9
    ) = Warehouse(
        id = id,
        name = name,
        regionalZone = regionalZone,
        longitude = longitude,
        latitude = latitude
    )
}
