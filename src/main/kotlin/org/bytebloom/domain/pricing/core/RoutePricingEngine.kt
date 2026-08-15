package org.bytebloom.domain.pricing.core

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.pricing.strategy.DispatchStrategy

class RoutePricingEngine(private var strategy: DispatchStrategy) {
    fun setStrategy(newStrategy: DispatchStrategy) {
        strategy = newStrategy
    }

    private fun findRoute(
        pkg: Package,
        availableRoutes: List<Route>
    ): Route? {
        return availableRoutes.firstOrNull {
            it.originWarehouse.id == pkg.originWarehouse.id &&
                    it.destinationWarehouse.id == pkg.destinationWarehouse.id
        }
    }

    fun calculateShippingCost(
        pkg: Package,
        availableRoutes: List<Route>
    ): Double? {

        val matchingRoute = findRoute(
            pkg,
            availableRoutes
        ) ?: return null

        val baseCost = strategy.calculateTransitCost(
            matchingRoute,
            pkg
        )

        return baseCost * strategy.getPriorityMultiplier(pkg)
    }
}