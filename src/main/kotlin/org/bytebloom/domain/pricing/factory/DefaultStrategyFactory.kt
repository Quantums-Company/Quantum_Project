package org.bytebloom.domain.pricing.factory

import org.bytebloom.domain.pricing.strategy.DispatchStrategy
import org.bytebloom.domain.pricing.strategy.EcoStrategy
import org.bytebloom.domain.pricing.strategy.ExpressStrategy
import org.bytebloom.domain.pricing.strategy.FragileStrategy
import org.bytebloom.domain.pricing.strategy.ShippingStrategyType

class DefaultStrategyFactory : StrategyFactory {
    private val strategies = mutableMapOf<ShippingStrategyType, DispatchStrategy>()

    init {
        registerStrategy(ShippingStrategyType.ECO, EcoStrategy())
        registerStrategy(ShippingStrategyType.EXPRESS, ExpressStrategy())
        registerStrategy(ShippingStrategyType.FRAGILE, FragileStrategy())
        // any strategy can add here only
    }

    override fun getStrategy(strategyType: ShippingStrategyType): DispatchStrategy? {
        return strategies[strategyType]
    }

    override fun registerStrategy(type: ShippingStrategyType, strategy: DispatchStrategy) {
        strategies[type] = strategy
    }
}