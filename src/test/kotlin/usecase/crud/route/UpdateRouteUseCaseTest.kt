package usecase.crud.route

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.exception.DatabaseConflictException
import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.exception.ResourceNotFoundException
import org.bytebloom.domain.model.input.update.RouteUpdateInput
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.usecase.crud.route.UpdateRouteUseCase
import org.bytebloom.domain.validator.update.UpdateRouteValidator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class UpdateRouteUseCaseTest {

    private lateinit var routeRepository: RouteRepository
    private lateinit var updateRoute: UpdateRouteUseCase

    @BeforeEach
    fun setUp() {
        routeRepository = mockk()
        coEvery { routeRepository.getById(ROUTE_ID) } returns createExistingRoute()
        coEvery { routeRepository.update(any()) } answers { firstArg() }
        updateRoute = UpdateRouteUseCase(routeRepository, UpdateRouteValidator())
    }

    @Test
    fun `should update only provided field and keep the others unchanged`() = runTest {
        // Given
        val input = RouteUpdateInput(id = ROUTE_ID, distanceKm = 200.0)

        // When
        val updatedRoute = updateRoute(input)

        // Then
        assertThat(updatedRoute.distanceKm).isEqualTo(200.0)
        assertThat(updatedRoute.typicalDelayMin).isEqualTo(EXISTING_DELAY_MIN)
        assertThat(updatedRoute.originWarehouse).isEqualTo(ORIGIN)
        assertThat(updatedRoute.destinationWarehouse).isEqualTo(DESTINATION)
    }

    @Test
    fun `should update multiple fields when they are provided`() = runTest {
        // Given
        val newDestination = createWarehouse(id = "WH-003", name = "Hebron Depot")
        val input = RouteUpdateInput(id = ROUTE_ID, typicalDelayMin = 30, destinationWarehouse = newDestination)

        // When
        val updatedRoute = updateRoute(input)

        // Then
        assertThat(updatedRoute.typicalDelayMin).isEqualTo(30)
        assertThat(updatedRoute.destinationWarehouse).isEqualTo(newDestination)
    }

    @Test
    fun `should save updated route through repository exactly once`() = runTest {
        // Given
        val input = RouteUpdateInput(id = ROUTE_ID, distanceKm = 200.0)

        // When
        updateRoute(input)

        // Then
        coVerify(exactly = 1) {
            routeRepository.update(match { it.id == ROUTE_ID && it.distanceKm == 200.0 })
        }
    }

    @Test
    fun `should throw validation exception and skip repository when no fields are provided`() = runTest {
        // Given
        val input = RouteUpdateInput(id = ROUTE_ID)

        // When & Then
        assertFailsWith<EntityValidationException> { updateRoute(input) }
        coVerify(exactly = 0) { routeRepository.getById(any()) }
        coVerify(exactly = 0) { routeRepository.update(any()) }
    }

    @Test
    fun `should throw validation exception and skip repository when id is invalid`() = runTest {
        // Given
        val input = RouteUpdateInput(id = "WH-001", distanceKm = 200.0)

        // When & Then
        assertFailsWith<EntityValidationException> { updateRoute(input) }
        coVerify(exactly = 0) { routeRepository.update(any()) }
    }

    @Test
    fun `should throw not found exception and skip update when route does not exist`() = runTest {
        // Given
        coEvery { routeRepository.getById(ROUTE_ID) } returns null
        val input = RouteUpdateInput(id = ROUTE_ID, distanceKm = 200.0)

        // When & Then
        assertFailsWith<ResourceNotFoundException> { updateRoute(input) }
        coVerify(exactly = 0) { routeRepository.update(any()) }
    }

    @Test
    fun `should throw validation exception when new destination equals origin`() = runTest {
        // Given
        val input = RouteUpdateInput(id = ROUTE_ID, destinationWarehouse = ORIGIN)

        // When & Then
        assertFailsWith<EntityValidationException> { updateRoute(input) }
        coVerify(exactly = 0) { routeRepository.update(any()) }
    }

    @Test
    fun `should propagate conflict exception when repository update fails`() = runTest {
        // Given
        coEvery { routeRepository.update(any()) } throws DatabaseConflictException("Route was modified")
        val input = RouteUpdateInput(id = ROUTE_ID, distanceKm = 200.0)

        // When & Then
        assertFailsWith<DatabaseConflictException> { updateRoute(input) }
    }

    private fun createExistingRoute() = Route(
        id = ROUTE_ID,
        distanceKm = 120.0,
        typicalDelayMin = EXISTING_DELAY_MIN,
        originWarehouse = ORIGIN,
        destinationWarehouse = DESTINATION
    )

    private companion object {
        const val ROUTE_ID = "RT-001"
        const val EXISTING_DELAY_MIN = 15
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