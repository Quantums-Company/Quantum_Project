package org.bytebloom.logiroute.domain.routing.bfs

import org.bytebloom.logiroute.domain.model.Warehouse
import org.bytebloom.logiroute.domain.routing.WarehouseGraph

class BfsBenchmark(private val warehouseGraph: WarehouseGraph) {

    companion object {
        private const val NANOS_PER_MILLISECOND = 1_000_000.0
        private const val PERCENTAGE_MULTIPLIER = 100.0
    }

    private data class RunResult(
        val label: String,
        val path: List<Warehouse>?,
        val evaluatedWarehouses: Int,
        val elapsedNanos: Long
    )

    fun runAndCompare(startWarehouse: Warehouse, endWarehouse: Warehouse): String {
        val unidirectionalResult = runStandardBfs(startWarehouse, endWarehouse)
        val bidirectionalResult = runBidirectionalBfs(startWarehouse, endWarehouse)
        return buildReport(
            startWarehouse,
            endWarehouse,
            unidirectionalResult,
            bidirectionalResult
        )
    }

    private fun runStandardBfs(startWarehouse: Warehouse, destinationWarehouse: Warehouse): RunResult {
        val router = UnidirectionalBreadthFirstRouter(warehouseGraph)
        val (path, elapsedNanos) = measure { router.findShortestPath(startWarehouse, destinationWarehouse) }
        return RunResult("Unidirectional BFS", path, router.evaluatedWarehouses, elapsedNanos)
    }

    private fun runBidirectionalBfs(startWarehouse: Warehouse, destinationWarehouse: Warehouse): RunResult {
        val router = BidirectionalBreadthFirstRouter(warehouseGraph)
        val (path, elapsedNanos) = measure { router.findShortestPath(startWarehouse, destinationWarehouse) }
        return RunResult("Bidirectional BFS", path, router.evaluatedWarehouses, elapsedNanos)
    }

    private fun <T> measure(block: () -> T): Pair<T, Long> {
        val startedAtNanos = System.nanoTime()
        val measuredValue = block()
        return measuredValue to (System.nanoTime() - startedAtNanos)
    }

    private fun buildReport(
        startWarehouse: Warehouse,
        destinationWarehouse: Warehouse,
        unidirectional: RunResult,
        bidirectional: RunResult
    ): String = buildString {
        appendLine()
        appendLine("==================== BFS Benchmark ====================")
        appendLine("Route: ${startWarehouse.id} -> ${destinationWarehouse.id}")
        appendLine("---------------------------------------------------------")
        appendLine(String.format("%-20s %12s %14s %12s", "Algorithm", "Evaluated", "Path Length", "Time (ms)"))
        appendLine("---------------------------------------------------------")
        appendLine(rowFor(unidirectional))
        appendLine(rowFor(bidirectional))
        appendLine("---------------------------------------------------------")
        appendLine(efficiencyLine(unidirectional, bidirectional))
        appendLine(correctnessLine(unidirectional, bidirectional))
        appendLine("===========================================================")
    }

    private fun rowFor(measuredValue: RunResult): String {
        val routeLength = measuredValue.path?.size ?: 0
        val elapsedMillis = measuredValue.elapsedNanos / NANOS_PER_MILLISECOND
        return String.format(
            "%-20s %12d %14d %12.3f",
            measuredValue.label,
            measuredValue.evaluatedWarehouses,
            routeLength,
            elapsedMillis
        )
    }

    private fun efficiencyLine(unidirectional: RunResult, bidirectional: RunResult): String {
        if (unidirectional.evaluatedWarehouses == 0) return ""
        val evaluationReductionPercent = PERCENTAGE_MULTIPLIER *
                (1.0 - bidirectional.evaluatedWarehouses.toDouble() / unidirectional.evaluatedWarehouses)
        return "Bidirectional BFS evaluated %.1f%% fewer warehouses than Unidirectional BFS."
            .format(evaluationReductionPercent)
    }

    private fun correctnessLine(unidirectional: RunResult, bidirectional: RunResult): String {
        val unidirectionalLength = unidirectional.path?.size ?: 0
        val bidirectionalLength = bidirectional.path?.size ?: 0
        return if (unidirectionalLength != bidirectionalLength) {
            "Path length mismatch detected: Unidirectional BFS returned $unidirectionalLength warehouse(s), " +
                    "bidirectional BFS returned $bidirectionalLength. Both algorithms must agree — investigate."
        } else {
            "Correctness check passed: both algorithms agree on a path length of $unidirectionalLength."
        }
    }
}