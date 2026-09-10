package org.bytebloom.domain.pricing.strategy

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Route

class FragileStrategy : DispatchStrategy {
    companion object {
        private const val RATE_PER_KM = 2.5
        private const val WEIGHT_MULTIPLIER = 1.5
        private const val FRAGILE_PRIORITY = 1.25
        private const val DELAY_FACTOR = 0.5
    }
    override fun calculateTransitCost(route: Route, pkg: Package): Double {
        return (route.distanceKm * RATE_PER_KM) +
                (pkg.weight * WEIGHT_MULTIPLIER) +
                (route.typicalDelayMin * DELAY_FACTOR)
    }
    override fun getPriorityMultiplier(pkg: Package): Double = FRAGILE_PRIORITY
}