package org.bytebloom.domain.pricing

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Route

interface DispatchStrategy {
    fun calculateTransitCost(route: Route, pkg: org.bytebloom.domain.model.Package): Double
    fun getPriorityMultiplier(pkg: Package): Double
}