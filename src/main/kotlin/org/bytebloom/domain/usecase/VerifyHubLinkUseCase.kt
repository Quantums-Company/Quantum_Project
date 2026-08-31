package org.bytebloom.quantum.usecase

class VerifyHubLinkUseCase {
    operator fun invoke(
        connections: Map<String, List<String>>,
        origin: String,
        target: String
    ): Boolean {
        return origin == target || search(
            connections,
            listOf(origin),
            listOf(target),
            setOf(origin),
            setOf(target)
        )
    }

    private tailrec fun search(
        connections: Map<String, List<String>>,
        fromFront: List<String>,
        toFront: List<String>,
        fromVisited: Set<String>,
        toVisited: Set<String>
    ): Boolean {
        val fromNext = fromFront
            .flatMap { connections[it] ?: emptyList() }
            .minus(fromVisited)

        val toNext = toFront
            .flatMap { connections[it] ?: emptyList() }
            .minus(toVisited)

        val allFrom = fromVisited + fromNext
        val allTo = toVisited + toNext
        val hit = allFrom.any(allTo::contains)
        val dead = fromNext.isEmpty() && toNext.isEmpty()

        return hit || dead.not() && search(connections, fromNext, toNext, allFrom, allTo)
    }
}