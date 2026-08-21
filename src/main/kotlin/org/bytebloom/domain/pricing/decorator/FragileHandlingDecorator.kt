package org.bytebloom.domain.pricing.decorator

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.pricing.core.PackageComponent
import org.bytebloom.util.Logger

class FragileHandlingDecorator(
    component: PackageComponent,
    fee: Double
) : PackageDecorator(component) {

    private val fee: Double = if (fee < 0.0) {
        Logger.warning("Fragile handling fee cannot be negative. Using 0.0 instead.")
        0.0
    } else {
        fee
    }

    override fun getTransitRate(pkg: Package): Double? {
        return super.getTransitRate(pkg)?.plus(fee)
    }
}
