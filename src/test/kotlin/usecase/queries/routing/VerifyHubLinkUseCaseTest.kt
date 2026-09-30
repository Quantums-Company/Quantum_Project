package usecase.queries.routing

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.routing.common.RouteFinder
import org.bytebloom.domain.usecase.queries.routing.VerifyHubLinkUseCase
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class VerifyHubLinkUseCaseTest {

    private val routeFinder = mockk<RouteFinder>()
    private val useCase = VerifyHubLinkUseCase(routeFinder)

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
    fun `when route exists between warehouses should return true`() {
        // Given
        val path = listOf(origin, destination)
        every { routeFinder.findShortestPath(origin, destination) } returns path

        // When
        val result = useCase(origin, destination)

        // Then
        assertTrue(result)
        verify(exactly = 1) { routeFinder.findShortestPath(origin, destination) }
    }
}