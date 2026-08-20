package org.bytebloom.domain.pricing.decorator

import org.bytebloom.domain.model.Package
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

    override fun getTransitRate(pkg: Package): Double? {
        return super.getTransitRate(pkg)?.plus(fee)
    }
}
