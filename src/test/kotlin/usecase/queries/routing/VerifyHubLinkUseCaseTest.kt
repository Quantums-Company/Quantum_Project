package usecase.queries.routing

import io.mockk.every
import io.mockk.mockk
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.routing.common.RouteFinder
import org.bytebloom.domain.usecase.queries.routing.VerifyHubLinkUseCase
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class VerifyHubLinkUseCaseTest {

    @Test
    fun `when route exists between warehouses should return true`() {
        // Given
        val routeFinder = mockk<RouteFinder>()
        val useCase = VerifyHubLinkUseCase(routeFinder)

        val origin = Warehouse(
            id = "WH-001",
            name = "Gaza Hub",
            regionalZone = "South",
            longitude = 34.4668,
            latitude = 31.5017
        )
        val destination = Warehouse(
            id = "WH-002",
            name = "North Hub",
            regionalZone = "North",
            longitude = 34.4668,
            latitude = 31.5500
        )

        // Intentionally return null to force a RED (failing assertion) state
        every { routeFinder.findShortestPath(origin, destination) } returns null

        // When
        val result = useCase(origin, destination)

        // Then
        assertTrue(result) // ❌ FAILS HERE: Expected true, but got false
    }
}