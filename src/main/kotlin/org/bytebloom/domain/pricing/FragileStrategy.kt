package org.bytebloom.domain.pricing

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Route

class FragileStrategy : DispatchStrategy {
    companion object {
        private const val RATE_PER_KM = 2.5
        private const val WEIGHT_MULTIPLIER = 1.5
        private const val FRAGILE_PRIORITY = 1.25
    }
    override fun calculateTransitCost(route: Route, pkg: org.bytebloom.domain.model.Package): Double {
        return (route.distanceKm * RATE_PER_KM) +
                (pkg.weight * WEIGHT_MULTIPLIER) +
                (route.typicalDelayMin * 0.5)
    }
    override fun getPriorityMultiplier(pkg: Package): Double = FRAGILE_PRIORITY
}