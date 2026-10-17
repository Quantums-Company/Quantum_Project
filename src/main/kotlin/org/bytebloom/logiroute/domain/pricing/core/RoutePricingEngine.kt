package org.bytebloom.logiroute.domain.pricing.core

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Route
import org.bytebloom.logiroute.domain.pricing.strategy.DispatchStrategy
import org.bytebloom.logiroute.domain.repository.RouteRepository

class RoutePricingEngine(
    private var strategy: DispatchStrategy,
    private val routeRepository: RouteRepository
) : PricingEngine {
    fun setStrategy(newStrategy: DispatchStrategy) {
        strategy = newStrategy
    }

    private suspend fun findRoute(pkg: Package): Route? {
        return routeRepository.getAll().firstOrNull {
            it.originWarehouse.id == pkg.originWarehouse.id &&
                    it.destinationWarehouse.id == pkg.destinationWarehouse.id
        }
    }

    override suspend fun calculateShippingCost(pkg: Package): Double? {
        val matchingRoute = findRoute(pkg) ?: return null

        val baseCost = strategy.calculateTransitCost(matchingRoute, pkg)

        return baseCost * strategy.getPriorityMultiplier(pkg)
    }
}