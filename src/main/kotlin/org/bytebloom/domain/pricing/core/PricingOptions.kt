package org.bytebloom.domain.pricing.core

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.pricing.decorator.DecoratorFee
import org.bytebloom.domain.pricing.strategy.ShippingStrategyType

data class PricingOptions(
    val pkg: Package,
    val strategy: ShippingStrategyType,
    val decorators: List<DecoratorFee> = emptyList()
)