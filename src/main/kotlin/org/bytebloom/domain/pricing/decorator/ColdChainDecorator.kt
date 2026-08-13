package org.bytebloom.domain.pricing.decorator

import org.bytebloom.domain.pricing.core.PackageComponent

class ColdChainDecorator(
    component: PackageComponent,
    private val multiplier: Double
) : PackageDecorator(component) {

    init {
        require(multiplier > 0) {
            "Cold chain multiplier must be greater than zero."
        }
    }

    override fun getTransitRate(): Double? {
        return super.getTransitRate()?.times(multiplier)
    }
}