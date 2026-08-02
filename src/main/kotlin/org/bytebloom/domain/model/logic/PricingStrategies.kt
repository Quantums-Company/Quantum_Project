package org.bytebloom.domain.model.logic

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Route

interface DispatchStrategy {
    fun calculateTransitCost(route: Route, pkg: Package): Double
    fun getPriorityMultiplier(pkg: Package): Double
}

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

class FragileStrategy : DispatchStrategy {
    companion object {
        private const val RATE_PER_KM = 2.5
        private const val WEIGHT_MULTIPLIER = 1.5
        private const val FRAGILE_PRIORITY = 1.25
    }
    override fun calculateTransitCost(route: Route, pkg: Package): Double {
        return (route.distanceKm * RATE_PER_KM) +
                (pkg.weight * WEIGHT_MULTIPLIER) +
                (route.typicalDelayMin * 0.5)
    }
    override fun getPriorityMultiplier(pkg: Package): Double = FRAGILE_PRIORITY
}

class RoutePricingEngine(private var strategy: DispatchStrategy) {
    fun setStrategy(newStrategy: DispatchStrategy) {
        strategy = newStrategy
    }

    private fun findRoute(
        pkg: Package,
        availableRoutes: List<Route>
    ): Route {
        val route = requireNotNull(
            availableRoutes.find {
                it.originWarehouse.id == pkg.originWarehouse.id &&
                        it.destinationWarehouse.id == pkg.destinationWarehouse.id
            }
        ) {
            "No direct route found between ${pkg.originWarehouse.id} and ${pkg.destinationWarehouse.id}"
        }
        return route
    }

    fun calculateShippingCost(
        pkg: Package,
        availableRoutes: List<Route>
    ): Double {
        val matchingRoute = findRoute(pkg,availableRoutes)

        val baseCost = strategy.calculateTransitCost(matchingRoute, pkg)

        return baseCost * strategy.getPriorityMultiplier(pkg)
    }
}