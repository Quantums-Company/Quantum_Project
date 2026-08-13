package org.bytebloom.domain.pricing.strategy

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Route

class ExpressStrategy : DispatchStrategy {
    companion object {
        private const val RATE_PER_KM = 4.0
        private const val WEIGHT_MULTIPLIER = 2.0
        private const val DELAY_WEIGHT = 1.0
        private const val PRIORITY_BOOST = 1.75
    }
    override fun calculateTransitCost(route: Route, pkg: Package): Double {
        return (route.distanceKm * RATE_PER_KM) +
                (pkg.weight * WEIGHT_MULTIPLIER) +
                (route.typicalDelayMin * DELAY_WEIGHT)
    }
    override fun getPriorityMultiplier(pkg: Package): Double = PRIORITY_BOOST
}