package org.bytebloom.domain.pricing.core

import org.bytebloom.domain.model.Package

class BasePackageComponent(
    private val pricingEngine: PricingEngine
) : PackageComponent {
    override fun getTransitRate(pkg: Package): Double? = pricingEngine.calculateShippingCost(pkg)
}