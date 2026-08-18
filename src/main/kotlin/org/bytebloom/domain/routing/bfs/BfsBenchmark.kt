package org.bytebloom.domain.routing.bfs

import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.routing.WarehouseGraph
import org.bytebloom.util.Logger

class BfsBenchmark(
    private val graph: WarehouseGraph) {

    private data class RunResult(
        val label: String,
        val path: List<Warehouse>?,
        val evaluatedWarehouses: Int,
        val elapsedNanos: Long
    )

    fun runAndCompare(startWarehouse: Warehouse, endWarehouse: Warehouse) {
        val unidirectionalResult = runStandardBfs(startWarehouse, endWarehouse)
        val bidirectionalResult = runBidirectionalBfs(startWarehouse, endWarehouse)

        report(startWarehouse, endWarehouse, unidirectionalResult, bidirectionalResult)
    }

    private fun runStandardBfs(start: Warehouse, end: Warehouse): RunResult {
        val router = UnidirectionalBreadthFirstRouter(graph)
        val (path, elapsedNanos) = measure { router.findShortestPath(start, end) }
        return RunResult("Unidirectional BFS", path, router.evaluatedWarehouses, elapsedNanos)
    }

    private fun runBidirectionalBfs(start: Warehouse, end: Warehouse): RunResult {
        val router = BidirectionalBreadthFirstRouter(graph)
        val (path, elapsedNanos) = measure { router.findShortestPath(start, end) }
        return RunResult("Bidirectional BFS", path, router.evaluatedWarehouses, elapsedNanos)
    }

    private fun <T> measure(block: () -> T): Pair<T, Long> {
        val startTime = System.nanoTime()
        val result = block()
        return result to (System.nanoTime() - startTime)
    }

    private fun report(
        start: Warehouse,
        end: Warehouse,
        unidirectional: RunResult,
        bidirectional: RunResult
    ) {
        Logger.info("")
        Logger.info("==================== BFS Benchmark ====================")
        Logger.info("Route: ${start.id} -> ${end.id}")
        Logger.info("---------------------------------------------------------")
        Logger.info(String.format("%-20s %12s %14s %12s", "Algorithm", "Evaluated", "Path Length", "Time (ms)"))
        Logger.info("---------------------------------------------------------")

        logRow(unidirectional)
        logRow(bidirectional)

        Logger.info("---------------------------------------------------------")
        logEfficiency(unidirectional, bidirectional)
        logCorrectnessCheck(unidirectional, bidirectional)
        Logger.info("===========================================================")
        Logger.info("")
    }

    private fun logRow(result: RunResult) {
        val pathLength = result.path?.size ?: 0
        val elapsedMs = result.elapsedNanos / 1_000_000.0

        Logger.info(
            String.format(
                "%-20s %12d %14d %12.3f",
                result.label,
                result.evaluatedWarehouses,
                pathLength,
                elapsedMs
            )
        )
    }

    private fun logEfficiency(unidirectional: RunResult, bidirectional: RunResult) {
        if (unidirectional.evaluatedWarehouses == 0) {
            return
        }

        val reduction =
            100.0 * (1.0 - bidirectional.evaluatedWarehouses.toDouble() / unidirectional.evaluatedWarehouses)

        Logger.info(
            "Bidirectional BFS evaluated %.1f%% fewer warehouses than Unidirectional BFS."
                .format(reduction)
        )
    }

    private fun logCorrectnessCheck(unidirectional: RunResult, bidirectional: RunResult) {
        val unidirectionalLength = unidirectional.path?.size ?: 0
        val bidirectionalLength = bidirectional.path?.size ?: 0

        if (unidirectionalLength != bidirectionalLength) {
            Logger.warning(
                "Path length mismatch detected: Unidirectional BFS returned $unidirectionalLength " +
                        "warehouse(s), bidirectional BFS returned $bidirectionalLength. " +
                        "Both algorithms must agree on shortest path length - investigate."
            )
        } else {
            Logger.info("Correctness check passed: both algorithms agree on a path length of $unidirectionalLength.")
        }
    }

}