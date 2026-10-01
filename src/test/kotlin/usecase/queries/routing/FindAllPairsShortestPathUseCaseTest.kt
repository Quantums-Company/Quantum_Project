package usecase.queries.routing

import org.junit.jupiter.api.Test
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.routing.WarehouseGraph
import org.bytebloom.domain.routing.common.RouteFinder
import org.bytebloom.domain.usecase.queries.routing.FindAllPairsShortestPathUseCase
import org.junit.jupiter.api.BeforeEach
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FindAllPairsShortestPathUseCaseTest {

    private val routeFinder = mockk<RouteFinder>()
    private lateinit var graph: WarehouseGraph
    private lateinit var useCase: FindAllPairsShortestPathUseCase

    private val warehouseA = Warehouse(
        id = "WH-001",
        name = "A",
        regionalZone = "North",
        longitude = 34.0,
        latitude = 31.0
    )
    private val warehouseB = Warehouse(
        id = "WH-002",
        name = "B",
        regionalZone = "Central",
        longitude = 34.1,
        latitude = 31.1
    )
    private val warehouseC = Warehouse(
        id = "WH-003",
        name = "C",
        regionalZone = "South",
        longitude = 34.2,
        latitude = 31.2
    )

    @BeforeEach
    fun setUp() {
        graph = WarehouseGraph()
        // One-directional: A -> B (5km) -> C (3km). No reverse edges, no A->C direct edge.
        graph.addRoute(warehouseA, warehouseB, distanceKm = 5.0)
        graph.addRoute(warehouseB, warehouseC, distanceKm = 3.0)
        useCase = FindAllPairsShortestPathUseCase(routeFinder, graph)
    }

    @Test
    fun `distance from a warehouse to itself is always zero, without asking RouteFinder`() {
        // Given — no stubs needed for A->A, B->B, C->C

        // When
        val result = useCase()

        // Then
        assertEquals(0.0, result.getValue(warehouseA).getValue(warehouseA))
        assertEquals(0.0, result.getValue(warehouseB).getValue(warehouseB))
        assertEquals(0.0, result.getValue(warehouseC).getValue(warehouseC))
        verify(exactly = 0) { routeFinder.findShortestPath(warehouseA, warehouseA) }
    }

    @Test
    fun `sums real graph edge weights along the path RouteFinder returns`() {
        // Given
        every {
            routeFinder.findShortestPath(warehouseA, warehouseB)
        } returns listOf(warehouseA, warehouseB)
        every {
            routeFinder.findShortestPath(warehouseA, warehouseC)
        } returns listOf(warehouseA, warehouseB, warehouseC)
        every {
            routeFinder.findShortestPath(warehouseB, warehouseC)
        } returns listOf(warehouseB, warehouseC)
        every {
            routeFinder.findShortestPath(warehouseB, warehouseA)
        } returns null
        every {
            routeFinder.findShortestPath(warehouseC, warehouseA)
        } returns null
        every {
            routeFinder.findShortestPath(warehouseC, warehouseB)
        } returns null

        // When
        val result = useCase()

        // Then
        assertEquals(5.0, result.getValue(warehouseA).getValue(warehouseB))
        assertEquals(8.0, result.getValue(warehouseA).getValue(warehouseC))   // 5.0 + 3.0, summed from the real graph, not RouteFinder
        assertEquals(3.0, result.getValue(warehouseB).getValue(warehouseC))
    }

    @Test
    fun `when RouteFinder reports no path, distance is positive infinity`() {
        // Given — one-directional graph, so B->A genuinely has no path
        every {
            routeFinder.findShortestPath(warehouseB, warehouseA)
        } returns null
        every {
            routeFinder.findShortestPath(warehouseA, warehouseB)
        } returns listOf(warehouseA, warehouseB)
        every {
            routeFinder.findShortestPath(warehouseA, warehouseC)
        } returns listOf(warehouseA, warehouseB, warehouseC)
        every {
            routeFinder.findShortestPath(warehouseB, warehouseC)
        } returns listOf(warehouseB, warehouseC)
        every {
            routeFinder.findShortestPath(warehouseC, warehouseA)
        } returns null
        every {
            routeFinder.findShortestPath(warehouseC, warehouseB)
        } returns null

        // When
        val result = useCase()

        // Then
        assertTrue(result.getValue(warehouseB).getValue(warehouseA).isInfinite())
    }

    @Test
    fun `when a hop in the returned path has no matching edge weight in the graph, that segment is treated as infinite`() {
        // Given — RouteFinder claims a direct A->C hop, but no such edge was ever added to the graph
        every {
            routeFinder.findShortestPath(warehouseA, warehouseC)
        } returns listOf(warehouseA, warehouseC)
        every {
            routeFinder.findShortestPath(warehouseA, warehouseB)
        } returns listOf(warehouseA, warehouseB)
        every {
            routeFinder.findShortestPath(warehouseB, warehouseC)
        } returns listOf(warehouseB, warehouseC)
        every {
            routeFinder.findShortestPath(warehouseB, warehouseA)
        } returns null
        every {
            routeFinder.findShortestPath(warehouseC, warehouseA)
        } returns null
        every {
            routeFinder.findShortestPath(warehouseC, warehouseB)
        } returns null

        // When
        val result = useCase()

        // Then
        assertTrue(result.getValue(warehouseA).getValue(warehouseC).isInfinite())
    }

    @Test
    fun `an empty graph returns an empty result and never calls RouteFinder`() {
        // Given
        val emptyGraph = WarehouseGraph()
        val emptyUseCase = FindAllPairsShortestPathUseCase(routeFinder, emptyGraph)

        // When
        val result = emptyUseCase()

        // Then
        assertTrue(result.isEmpty())
        verify(exactly = 0) { routeFinder.findShortestPath(any(), any()) }
    }
}
