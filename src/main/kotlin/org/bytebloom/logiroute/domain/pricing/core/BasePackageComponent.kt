package org.bytebloom.logiroute.domain.pricing.core

import org.bytebloom.logiroute.domain.model.Package

class BasePackageComponent(
    private val pricingEngine: PricingEngine
) : PackageComponent {
    override suspend fun getTransitRate(pkg: Package): Double? = pricingEngine.calculateShippingCost(pkg)
}