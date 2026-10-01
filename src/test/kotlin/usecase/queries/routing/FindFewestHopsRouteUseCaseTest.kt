package usecase.queries.routing

import org.junit.jupiter.api.Test
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.routing.common.RouteFinder
import org.bytebloom.domain.usecase.queries.routing.FindFewestHopsRouteUseCase
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FindFewestHopsRouteUseCaseTest {

    private val routeFinder = mockk<RouteFinder>()
    private val useCase = FindFewestHopsRouteUseCase(routeFinder)

    private val origin = Warehouse(
        id = "WH-001",
        name = "Gaza Hub",
        regionalZone = "South",
        longitude = 34.4668,
        latitude = 31.5017
    )
    private val waypoint = Warehouse(
        id = "WH-002",
        name = "Mid Hub",
        regionalZone = "Central",
        longitude = 34.4700,
        latitude = 31.5200
    )
    private val destination = Warehouse(
        id = "WH-003",
        name = "North Hub",
        regionalZone = "North",
        longitude = 34.4668,
        latitude = 31.5500
    )

    @Test
    fun `when a path exists should return the exact path from RouteFinder`() {
        // Given
        val expectedPath = listOf(origin, waypoint, destination)
        every { routeFinder.findShortestPath(origin, destination) } returns expectedPath

        // When
        val result = useCase(origin, destination)

        // Then
        assertEquals(expectedPath, result)
        verify(exactly = 1) { routeFinder.findShortestPath(origin, destination) }
    }

    @Test
    fun `when no path exists should return null`() {
        // Given
        every { routeFinder.findShortestPath(origin, destination) } returns null

        // When
        val result = useCase(origin, destination)

        // Then
        assertNull(result)
        verify(exactly = 1) { routeFinder.findShortestPath(origin, destination) }
    }

    @Test
    fun `when origin and destination are the same warehouse should still delegate to RouteFinder`() {
        // Given
        every { routeFinder.findShortestPath(origin, origin) } returns listOf(origin)

        // When
        val result = useCase(origin, origin)

        // Then
        assertEquals(listOf(origin), result)
        verify(exactly = 1) { routeFinder.findShortestPath(origin, origin) }
    }

    @Test
    fun `does not transform or reorder the path returned by RouteFinder`() {
        // Given — a deliberately "odd" path (goes through destination then back) to prove zero post-processing
        val oddPath = listOf(origin, destination, waypoint, destination)
        every { routeFinder.findShortestPath(origin, destination) } returns oddPath

        // When
        val result = useCase(origin, destination)

        // Then
        assertEquals(oddPath, result)
    }
}
