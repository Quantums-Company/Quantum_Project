package org.bytebloom.logiroute.presentation

import org.bytebloom.logiroute.domain.model.Warehouse

/** The answer of the shortest-route search, ready to be shown. */
data class RoutePlan(
    val stops: List<Warehouse>,
    val legs: List<RouteLeg>,
    val totalDistanceKm: Double
)
