package org.bytebloom.domain.pricing.decorator

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.pricing.core.PackageComponent
import org.bytebloom.util.Logger

class ColdChainDecorator(
    component: PackageComponent,
    multiplier: Double
) : PackageDecorator(component) {

    private val multiplier: Double = if (multiplier <= 0.0){
        Logger.warning("Cold chain multiplier must be greater than 0. Using 1.0 instead.")
        1.0
    } else {
        multiplier
    }

    override fun getTransitRate(pkg: Package): Double? {
        return super.getTransitRate(pkg)?.times(multiplier)
    }
}