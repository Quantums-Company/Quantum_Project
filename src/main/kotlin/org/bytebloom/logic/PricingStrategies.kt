package org.bytebloom.logic

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Route

interface DispatchStrategy {
    fun calculateTransitCost(route: Route, pkg: Package): Double
    fun getPriorityMultiplier(pkg: Package): Double
}

class ExpressStrategy : DispatchStrategy {
    override fun calculateTransitCost(route: Route, pkg: Package): Double {
        return 0.0
    }
      override fun getPriorityMultiplier(pkg: Package): Double{
          return 1.0
      }
}

class EcoStrategy : DispatchStrategy {
    override fun calculateTransitCost(route: Route, pkg: Package): Double {
        return 0.0
    }
    override fun getPriorityMultiplier(pkg: Package): Double{
        return 1.0
    }
}

class FragileStrategy : DispatchStrategy {
    override fun calculateTransitCost(route: Route, pkg: Package): Double {
        return 0.0
    }
    override fun getPriorityMultiplier(pkg: Package): Double{
        return 1.0
    }
}

class RoutePricingEngine(var strategy: DispatchStrategy) {
    fun executePricing(route: Route, pkg: Package): Double {
        return strategy.calculateTransitCost(route, pkg)
    }
    fun setStrategy(strategy: DispatchStrategy) {
        this.strategy = strategy
    }
}