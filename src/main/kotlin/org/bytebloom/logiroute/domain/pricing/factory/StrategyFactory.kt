package org.bytebloom.logiroute.domain.pricing.factory

import org.bytebloom.logiroute.domain.pricing.strategy.DispatchStrategy
import org.bytebloom.logiroute.domain.pricing.strategy.ShippingStrategyType

interface StrategyFactory {
    fun getStrategy(strategyType: ShippingStrategyType): DispatchStrategy?
    fun registerStrategy(type: ShippingStrategyType, strategy: DispatchStrategy)
}