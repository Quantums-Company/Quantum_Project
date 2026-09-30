package org.bytebloom.domain.routing

data class GraphBuildResult(
    val graph: WarehouseGraph,
    val skippedRouteIds: List<String>
)
