package org.bytebloom.domain.routing.bfs

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.routing.WarehouseGraph

class BfsBenchmark(
    private val graph: WarehouseGraph) {

    fun runAndCompare(startWarehouse: Warehouse, endWarehouse: Warehouse) {
        val standardRouter = UnidirectionalBreadthFirstRouter(graph)
        val standardPath = standardRouter.findShortestPath(startWarehouse, endWarehouse)
        val standardEvaluated = standardRouter.evaluatedWarehouses
        val bidirectionalRouter = BidirectionalBreadthFirstRouter(graph)
        val bidirectionalPath = bidirectionalRouter.findShortestPath(startWarehouse, endWarehouse)
        val bidirectionalEvaluated = bidirectionalRouter.evaluatedWarehouses

        println("\n=== BFS Benchmark ===")
        println("Standard BFS evaluated: $standardEvaluated")
        println("Bidirectional BFS evaluated: $bidirectionalEvaluated")
        println("----------------------------------------")
        println("Standard Path Length: ${standardPath?.size ?: 0}")
        println("Bidirectional Path Length: ${bidirectionalPath?.size ?: 0}")
        println("========================================\n")
    }
}