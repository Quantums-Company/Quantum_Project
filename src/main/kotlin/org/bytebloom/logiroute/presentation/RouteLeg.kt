package org.bytebloom.logiroute.presentation

import org.bytebloom.logiroute.domain.model.Warehouse

/** One hop of a route: from one warehouse to the next. */
data class RouteLeg(
    val from: Warehouse,
    val to: Warehouse,
    val distanceKm: Double
)