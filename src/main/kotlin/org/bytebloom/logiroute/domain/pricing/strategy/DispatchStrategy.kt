package org.bytebloom.logiroute.domain.pricing.strategy

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Route

interface DispatchStrategy {
    fun getPriorityMultiplier(pkg: Package): Double
    fun calculateTransitCost(route: Route, pkg: Package): Double
}