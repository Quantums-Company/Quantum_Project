package usecase.queries.routing

import org.junit.jupiter.api.Test
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.routing.common.RouteFinder
import org.bytebloom.domain.usecase.queries.routing.FindOptimalPathUseCase
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FindOptimalPathUseCaseTest {

    private val routeFinder = mockk<RouteFinder>()
    private val useCase = FindOptimalPathUseCase(routeFinder)

    private val origin = Warehouse(
        id = "WH-001",
        name = "Gaza Hub",
        regionalZone = "South",
        longitude = 34.4668,
        latitude = 31.5017
    )
    private val destination = Warehouse(
        id = "WH-002",
        name = "North Hub",
        regionalZone = "North",
        longitude = 34.4668,
        latitude = 31.5500
    )

    @Test
    fun `when a path exists should return the exact path from RouteFinder`() {
        // Given
        val expectedPath = listOf(origin, destination)
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
}
