package org.bytebloom.domain.pricing.factory

import org.bytebloom.domain.pricing.strategy.DispatchStrategy
import org.bytebloom.domain.pricing.strategy.ShippingStrategyType

interface StrategyFactory {
    fun getStrategy(strategyType: ShippingStrategyType): DispatchStrategy?
    fun registerStrategy(type: ShippingStrategyType, strategy: DispatchStrategy)
}