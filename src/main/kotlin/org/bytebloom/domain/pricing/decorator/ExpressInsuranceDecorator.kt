package org.bytebloom.domain.pricing.decorator

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.pricing.core.PackageComponent
import org.bytebloom.util.Logger

class ExpressInsuranceDecorator(
    component: PackageComponent,
    premium: Double
) : PackageDecorator(component) {

    private val premium: Double = if(premium < 0.0) {
        Logger.warning("Express insurance premium cannot be negative. Using 0.0 instead.")
        0.0
    } else {
        premium
    }

    override fun getTransitRate(pkg: Package): Double? {
        return super.getTransitRate(pkg)?.plus(premium)
    }
}
