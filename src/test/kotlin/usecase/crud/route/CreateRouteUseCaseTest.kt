package usecase.crud.route

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.exception.NetworkUnavailableException
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField
import org.bytebloom.domain.model.validation.ValidatorResult
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.service.IdGenerator
import org.bytebloom.domain.usecase.crud.route.CreateRouteUseCase
import org.bytebloom.domain.validator.create.CreateRouteValidator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class CreateRouteUseCaseTest {

    private lateinit var routeRepository: RouteRepository
    private lateinit var idGenerator: IdGenerator
    private lateinit var createRoute: CreateRouteUseCase

    @BeforeEach
    fun setUp() {
        routeRepository = mockk()
        idGenerator = mockk()
        every { idGenerator.next(EntityType.ROUTE) } returns GENERATED_ROUTE_ID
        coEvery { routeRepository.create(any()) } answers { firstArg() }
        createRoute = CreateRouteUseCase(routeRepository, CreateRouteValidator(), idGenerator)
    }

    @Test
    fun `should return created route with generated id when input is valid`() = runTest {
        // Given
        val distanceKm = 120.0

        // When
        val createdRoute = createRouteWith(distanceKm = distanceKm)

        // Then
        assertThat(createdRoute.id).isEqualTo(GENERATED_ROUTE_ID)
        assertThat(createdRoute.distanceKm).isEqualTo(distanceKm)
        assertThat(createdRoute.originWarehouse).isEqualTo(ORIGIN)
        assertThat(createdRoute.destinationWarehouse).isEqualTo(DESTINATION)
    }

    @Test
    fun `should save route through repository exactly once when input is valid`() = runTest {
        // Given
        val distanceKm = 120.0

        // When
        createRouteWith(distanceKm = distanceKm)

        // Then
        coVerify(exactly = 1) {
            routeRepository.create(match { it.id == GENERATED_ROUTE_ID && it.distanceKm == distanceKm })
        }
    }

    @Test
    fun `should request a new route id from id generator`() = runTest {
        // Given
        val distanceKm = 120.0

        // When
        createRouteWith(distanceKm = distanceKm)

        // Then
        verify(exactly = 1) { idGenerator.next(EntityType.ROUTE) }
    }

    @Test
    fun `should throw validation exception and skip saving when distance is not positive`() = runTest {
        // Given
        val invalidDistance = 0.0

        // When & Then
        assertFailsWith<EntityValidationException> { createRouteWith(distanceKm = invalidDistance) }
        coVerify(exactly = 0) { routeRepository.create(any()) }
    }

    @Test
    fun `should throw validation exception and skip saving when typical delay is negative`() = runTest {
        // Given
        val invalidDelay = -1

        // When & Then
        assertFailsWith<EntityValidationException> { createRouteWith(typicalDelayMin = invalidDelay) }
        coVerify(exactly = 0) { routeRepository.create(any()) }
    }

    @Test
    fun `should throw same warehouse violation when origin equals destination`() = runTest {
        // Given
        val sameWarehouse = ORIGIN

        // When
        val exception = assertFailsWith<EntityValidationException> {
            createRouteWith(destination = sameWarehouse)
        }

        // Then
        assertThat(exception.violations.single()).isInstanceOf(ValidatorError.SameWarehouse::class.java)
        coVerify(exactly = 0) { routeRepository.create(any()) }
    }

    @Test
    fun `should throw validation exception and skip saving when validator rejects route`() = runTest {
        // Given
        val rejectingValidator = mockk<CreateRouteValidator>()
        every { rejectingValidator(any()) } returns ValidatorResult.Invalid(
            listOf(ValidatorError.Custom(ValidatorField.ENTITY, "Rejected by validator"))
        )
        val createRouteWithRejection = CreateRouteUseCase(routeRepository, rejectingValidator, idGenerator)

        // When
        val exception = assertFailsWith<EntityValidationException> {
            createRouteWithRejection(120.0, 15, ORIGIN, DESTINATION)
        }

        // Then
        assertThat(exception.violations).hasSize(1)
        coVerify(exactly = 0) { routeRepository.create(any()) }
    }

    @Test
    fun `should propagate network exception when repository is unreachable`() = runTest {
        // Given
        coEvery { routeRepository.create(any()) } throws NetworkUnavailableException()

        // When & Then
        assertFailsWith<NetworkUnavailableException> { createRouteWith() }
    }

    private suspend fun createRouteWith(
        distanceKm: Double = 120.0,
        typicalDelayMin: Int = 15,
        origin: Warehouse = ORIGIN,
        destination: Warehouse = DESTINATION
    ): Route = createRoute(distanceKm, typicalDelayMin, origin, destination)

    private companion object {
        const val GENERATED_ROUTE_ID = "RT-001"
        val ORIGIN = createWarehouse(id = "WH-001", name = "Ramallah Hub")
        val DESTINATION = createWarehouse(id = "WH-002", name = "Nablus Depot")

        fun createWarehouse(id: String, name: String) = Warehouse(
            id = id,
            name = name,
            regionalZone = "West Bank",
            longitude = 35.2,
            latitude = 31.9
        )
    }
}