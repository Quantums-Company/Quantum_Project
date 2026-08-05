package org.bytebloom.domain.pricing

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Route

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