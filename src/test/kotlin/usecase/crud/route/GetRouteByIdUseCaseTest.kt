package usecase.crud.route

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.exception.NetworkUnavailableException
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.domain.usecase.crud.route.GetRouteByIdUseCase
import org.bytebloom.domain.validator.id.RouteIdValidator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertFailsWith

class GetRouteByIdUseCaseTest {

    private lateinit var routeRepository: RouteRepository
    private lateinit var getRouteById: GetRouteByIdUseCase

    @BeforeEach
    fun setUp() {
        routeRepository = mockk()
        getRouteById = GetRouteByIdUseCase(routeRepository, RouteIdValidator())
    }

    @Test
    fun `should return route when it exists in repository`() = runTest {
        // Given
        val existingRoute = createRoute()
        coEvery { routeRepository.getById(ROUTE_ID) } returns existingRoute

        // When
        val result = getRouteById(ROUTE_ID)

        // Then
        assertThat(result).isSameInstanceAs(existingRoute)
    }

    @Test
    fun `should return null when route does not exist`() = runTest {
        // Given
        coEvery { routeRepository.getById(ROUTE_ID) } returns null

        // When
        val result = getRouteById(ROUTE_ID)

        // Then
        assertThat(result).isNull()
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "WH-001", "RT-1"])
    fun `should throw validation exception and skip repository when id is invalid`(invalidId: String) = runTest {
        // Given: invalidId from @ValueSource

        // When & Then
        assertFailsWith<EntityValidationException> { getRouteById(invalidId) }
        coVerify(exactly = 0) { routeRepository.getById(any()) }
    }

    @Test
    fun `should propagate network exception when repository is unreachable`() = runTest {
        // Given
        coEvery { routeRepository.getById(ROUTE_ID) } throws NetworkUnavailableException()

        // When & Then
        assertFailsWith<NetworkUnavailableException> { getRouteById(ROUTE_ID) }
    }

    private fun createRoute() = Route(
        id = ROUTE_ID,
        distanceKm = 120.0,
        typicalDelayMin = 15,
        originWarehouse = createWarehouse(id = "WH-001"),
        destinationWarehouse = createWarehouse(id = "WH-002")
    )

    private fun createWarehouse(id: String) = Warehouse(
        id = id,
        name = "Test Warehouse",
        regionalZone = "West Bank",
        longitude = 35.2,
        latitude = 31.9
    )

    private companion object {
        const val ROUTE_ID = "RT-001"
    }
}
