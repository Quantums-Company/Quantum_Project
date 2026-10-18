package org.bytebloom.logiroute.domain.usecase.routing

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.routing.WarehouseGraph
import org.bytebloom.logiroute.domain.routing.common.RouteFinder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

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
        graph.addRoute(warehouseA, warehouseB, distanceKm = 5.0)
        graph.addRoute(warehouseB, warehouseC, distanceKm = 3.0)
        useCase = FindAllPairsShortestPathUseCase(routeFinder, graph)
    }

    @Test
    fun `should return zero distance from a warehouse to itself without asking route finder`() {
        // Given
        every { routeFinder.findShortestPath(any(), any()) } returns null

        // When
        val result = useCase()

        // Then
        assertThat(result.getValue(warehouseA).getValue(warehouseA)).isEqualTo(0.0)
        assertThat(result.getValue(warehouseB).getValue(warehouseB)).isEqualTo(0.0)
        assertThat(result.getValue(warehouseC).getValue(warehouseC)).isEqualTo(0.0)
        verify(exactly = 0) { routeFinder.findShortestPath(warehouseA, warehouseA) }
    }

    @Test
    fun `should sum graph edge weights along the path returned by route finder`() {
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
        assertThat(result.getValue(warehouseA).getValue(warehouseB)).isEqualTo(5.0)
        assertThat(result.getValue(warehouseA).getValue(warehouseC)).isEqualTo(8.0)
        assertThat(result.getValue(warehouseB).getValue(warehouseC)).isEqualTo(3.0)
    }

    @Test
    fun `should return infinite distance when route finder reports no path`() {
        // Given
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
        assertThat(result.getValue(warehouseB).getValue(warehouseA)).isPositiveInfinity()
    }

    @Test
    fun `should treat hop as infinite when edge weight is missing`() {
        // Given
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
        assertThat(result.getValue(warehouseA).getValue(warehouseC)).isPositiveInfinity()
    }

    @Test
    fun `should return empty result without calling route finder when graph is empty`() {
        // Given
        val emptyGraph = WarehouseGraph()
        val emptyUseCase = FindAllPairsShortestPathUseCase(routeFinder, emptyGraph)

        // When
        val result = emptyUseCase()

        // Then
        assertThat(result).isEmpty()
        verify(exactly = 0) { routeFinder.findShortestPath(any(), any()) }
    }
}
