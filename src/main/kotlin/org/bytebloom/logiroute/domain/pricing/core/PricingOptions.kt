package org.bytebloom.logiroute.domain.pricing.core

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.pricing.decorator.DecoratorFee
import org.bytebloom.logiroute.domain.pricing.strategy.ShippingStrategyType

data class PricingOptions(
    val pkg: Package,
    val strategy: ShippingStrategyType,
    val decorators: List<DecoratorFee> = emptyList()
)