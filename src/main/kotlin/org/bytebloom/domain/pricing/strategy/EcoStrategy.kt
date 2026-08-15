package org.bytebloom.domain.pricing.strategy

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Route

class EcoStrategy : DispatchStrategy {
    companion object {
        private const val RATE_PER_KM = 1.5
        private const val WEIGHT_MULTIPLIER = 0.5
        private const val STANDARD_PRIORITY = 1.0
    }
    override fun calculateTransitCost(route: Route, pkg: Package): Double {
        return (route.distanceKm * RATE_PER_KM) +
                (pkg.weight * WEIGHT_MULTIPLIER)
    }
    override fun getPriorityMultiplier(pkg: Package): Double = STANDARD_PRIORITY
}