package org.bytebloom.domain.pricing.core

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Route
import org.bytebloom.domain.pricing.strategy.DispatchStrategy
import org.bytebloom.domain.repository.RouteRepository

class RoutePricingEngine(
    private var strategy: DispatchStrategy,
    private val routeRepository: RouteRepository
) : PricingEngine {
    fun setStrategy(newStrategy: DispatchStrategy) {
        strategy = newStrategy
    }

    private fun findRoute(pkg: Package): Route? {
        return routeRepository.getAll().firstOrNull {
            it.originWarehouse.id == pkg.originWarehouse.id &&
                    it.destinationWarehouse.id == pkg.destinationWarehouse.id
        }
    }

    override fun calculateShippingCost(pkg: Package): Double? {
        val matchingRoute = findRoute(pkg) ?: return null

        val baseCost = strategy.calculateTransitCost(matchingRoute, pkg)

        return baseCost * strategy.getPriorityMultiplier(pkg)
    }
}