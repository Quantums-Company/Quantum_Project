package org.bytebloom.domain.pricing.decorator

import org.bytebloom.domain.pricing.core.PackageComponent

class ExpressInsuranceDecorator(
    component: PackageComponent,
    private val premium: Double
) : PackageDecorator(component) {

    init {
        require(premium >= 0) {
            "Insurance premium cannot be negative."
        }
    }

    override fun getTransitRate(): Double? {
        return super.getTransitRate()?.plus(premium)
    }
}
