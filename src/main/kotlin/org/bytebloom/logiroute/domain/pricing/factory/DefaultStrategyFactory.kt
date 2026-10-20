package org.bytebloom.logiroute.domain.pricing.factory

import org.bytebloom.logiroute.domain.pricing.strategy.DispatchStrategy
import org.bytebloom.logiroute.domain.pricing.strategy.EcoStrategy
import org.bytebloom.logiroute.domain.pricing.strategy.ExpressStrategy
import org.bytebloom.logiroute.domain.pricing.strategy.FragileStrategy
import org.bytebloom.logiroute.domain.pricing.strategy.ShippingStrategyType

class DefaultStrategyFactory : StrategyFactory {
    private val strategies = mutableMapOf<ShippingStrategyType, DispatchStrategy>()

    init {
        registerStrategy(ShippingStrategyType.ECO, EcoStrategy())
        registerStrategy(ShippingStrategyType.EXPRESS, ExpressStrategy())
        registerStrategy(ShippingStrategyType.FRAGILE, FragileStrategy())
    }

    override fun getStrategy(strategyType: ShippingStrategyType): DispatchStrategy? {
        return strategies[strategyType]
    }

    override fun registerStrategy(type: ShippingStrategyType, strategy: DispatchStrategy) {
        strategies[type] = strategy
    }
}