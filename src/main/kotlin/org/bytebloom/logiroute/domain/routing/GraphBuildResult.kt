package org.bytebloom.logiroute.domain.routing

data class GraphBuildResult(
    val graph: WarehouseGraph,
    val skippedRouteIds: List<String>
)
