package org.bytebloom.domain.pricing.strategy

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Route

interface DispatchStrategy {
    fun calculateTransitCost(route: Route, pkg: Package): Double
    fun getPriorityMultiplier(pkg: Package): Double
}