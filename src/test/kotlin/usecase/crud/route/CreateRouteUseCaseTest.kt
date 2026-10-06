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
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.service.IdGenerator
import org.bytebloom.domain.usecase.crud.route.CreateRouteUseCase
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
        createRoute = CreateRouteUseCase(routeRepository, idGenerator)
    }

    @Test
    fun `should return created route with generated id when input is valid`() = runTest {
        val distanceKm = 150.5
        val createdRoute = createRouteWith(distanceKm = distanceKm, typicalDelayMin = 20)

        assertThat(createdRoute.id).isEqualTo(GENERATED_ROUTE_ID)
        assertThat(createdRoute.distanceKm).isEqualTo(distanceKm)
        assertThat(createdRoute.originWarehouse).isEqualTo(ORIGIN)
        assertThat(createdRoute.destinationWarehouse).isEqualTo(DESTINATION)
    }

    @Test
    fun `should save route through repository exactly once when input is valid`() = runTest {
        createRouteWith(distanceKm = 100.0)

        coVerify(exactly = 1) {
            routeRepository.create(match { it.id == GENERATED_ROUTE_ID && it.distanceKm == 100.0 })
        }
    }

    @Test
    fun `should request a new route id from id generator`() = runTest {
        createRouteWith()
        verify(exactly = 1) { idGenerator.next(EntityType.ROUTE) }
    }

    @Test
    fun `should throw validation exception when user enters zero or negative distance`() = runTest {
        assertFailsWith<EntityValidationException> { createRouteWith(distanceKm = 0.0) }
        assertFailsWith<EntityValidationException> { createRouteWith(distanceKm = -45.0) }
        coVerify(exactly = 0) { routeRepository.create(any()) }
    }

    @Test
    fun `should throw validation exception when user enters negative typical delay`() = runTest {
        assertFailsWith<EntityValidationException> { createRouteWith(typicalDelayMin = -15) }
        coVerify(exactly = 0) { routeRepository.create(any()) }
    }

    @Test
    fun `should throw validation exception when origin and destination are the same warehouse`() = runTest {
        assertFailsWith<EntityValidationException> {
            createRouteWith(origin = ORIGIN, destination = ORIGIN)
        }
        coVerify(exactly = 0) { routeRepository.create(any()) }
    }

    @Test
    fun `should propagate network exception when repository is unreachable`() = runTest {
        coEvery { routeRepository.create(any()) } throws NetworkUnavailableException()

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