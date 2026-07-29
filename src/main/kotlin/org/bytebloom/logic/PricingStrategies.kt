package org.bytebloom.logic

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Route

interface DispatchStrategy {
    fun calculateTransitCost(route: Route, pkg: Package): Double
    fun getPriorityMultiplier(pkg: Package): Double
}

class ExpressStrategy : DispatchStrategy {
    override fun calculateTransitCost(route: Route, pkg: Package): Double {
        val baseRate = 5.0
        return (route.distanceKm * baseRate) + (pkg.weight * 3.0)
    }
      override fun getPriorityMultiplier(pkg: Package): Double{
          return 1.5
      }
}

class EcoStrategy : DispatchStrategy {
    override fun calculateTransitCost(route: Route, pkg: Package): Double {
        val baseRate = 2.0
        return (route.distanceKm * baseRate) + (pkg.weight * 0.5)
    }
    override fun getPriorityMultiplier(pkg: Package): Double{
        return 1.0
    }
}

class FragileStrategy : DispatchStrategy {
    override fun calculateTransitCost(route: Route, pkg: Package): Double {
        val baseRate = 3.5
        val safetyFee = 15.0
        return (route.distanceKm * baseRate) + (pkg.weight * 2.0) + safetyFee
    }
    override fun getPriorityMultiplier(pkg: Package): Double{
        return 1.2
    }
}

class RoutePricingEngine(var strategy: DispatchStrategy) {
    fun executePricing(route: Route, pkg: Package): Double {
        return strategy.calculateTransitCost(route, pkg)
    }
}