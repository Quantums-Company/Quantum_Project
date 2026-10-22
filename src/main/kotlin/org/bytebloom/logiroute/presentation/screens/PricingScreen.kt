package org.bytebloom.logiroute.presentation.screens

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.pricing.decorator.DecoratorFee
import org.bytebloom.logiroute.domain.pricing.strategy.ShippingStrategyType

interface PricingScreen : BaseScreen {
    fun showPackages(packages: List<Package>)
    fun showPrice(pkg: Package, strategy: ShippingStrategyType, decorators: List<DecoratorFee>, price: Double)
}
