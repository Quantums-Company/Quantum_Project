package org.bytebloom.logiroute.domain.routing

import org.bytebloom.logiroute.domain.model.Warehouse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WarehouseGraphImmutabilityTest {

    @Test
    fun `mutating adjacency map snapshot does not change graph state`() {
        val origin = warehouse("WH-001")
        val destination = warehouse("WH-002")
        val graph = WarehouseGraph().apply {
            addRoute(origin, destination, 12.5)
        }

        val exposedAdjacency = graph.adjacencyMap
        runCatching {
            (exposedAdjacency as MutableMap<Warehouse, Map<Warehouse, Double>>).clear()
        }
        runCatching {
            (exposedAdjacency.getValue(origin) as MutableMap<Warehouse, Double>).clear()
        }

        assertEquals(12.5, graph.neighbors(origin)[destination])
    }

    private fun warehouse(id: String) = Warehouse(
        id = id,
        name = id,
        regionalZone = "West Bank",
        longitude = 35.2,
        latitude = 31.9
    )
}
