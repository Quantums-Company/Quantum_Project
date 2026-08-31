package org.bytebloom.domain.model.usecase

import org.bytebloom.domain.model.DomainGraph

class FindNearestEmptyHubUseCase(
    private val graph: DomainGraph
) {

    data class Result(val id: String, val dist: Int)

    operator fun invoke(start: String): Result {
        val adj = graph.routes
            .groupBy { it.originWarehouse.id }
            .mapValues { (_, r) -> r.map { it.destinationWarehouse.id } }

        val empty = graph.warehouses
            .filter { w -> graph.packages.none { it.originWarehouse.id == w.id } }
            .map { it.id }
            .toSet()

        if (start in empty) return Result(start, 0)

        var fwd = listOf(start)
        var bwd = empty.toList()
        var fSeen = setOf(start)
        var bSeen = empty
        var dist = 1

        while (true) {
            val fNext = fwd.flatMap { adj[it].orEmpty() }.filter { it !in fSeen }
            val bNext = bwd.flatMap { adj[it].orEmpty() }.filter { it !in bSeen }

            val meet = (fNext.toSet() intersect bSeen) +
                    (bNext.toSet() intersect fSeen) +
                    (fNext.toSet() intersect bNext.toSet())

            if (meet.isNotEmpty()) return Result(meet.first(), dist)
            if (fNext.isEmpty() || bNext.isEmpty()) error("No empty hub found")

            fwd = fNext
            bwd = bNext
            fSeen += fNext
            bSeen += bNext
            dist++
        }
    }
}