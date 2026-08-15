package org.bytebloom.domain.pricing.decorator

import org.bytebloom.domain.pricing.core.PackageComponent

class FragileHandlingDecorator(
    component: PackageComponent,
    private val fee: Double
) : PackageDecorator(component) {

    init {
        require(fee >= 0) {
            "Fragile handling fee cannot be negative."
        }
    }

    override fun getTransitRate(): Double? {
        return super.getTransitRate()?.plus(fee)
    }
}
