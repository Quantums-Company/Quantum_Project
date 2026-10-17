package org.bytebloom.logiroute.domain.pricing.decorator

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.model.validation.ValidatorError
import org.bytebloom.logiroute.domain.model.validation.ValidatorField
import org.bytebloom.logiroute.domain.pricing.core.PackageComponent

class ColdChainDecorator(
    component: PackageComponent,
    private val multiplier: Double
) : PackageDecorator(component) {

    init {
        if (multiplier <= 0.0) {
            throw EntityValidationException(listOf(ValidatorError.NotPositive(ValidatorField.MULTIPLIER)))
        }
    }

    override suspend fun getTransitRate(pkg: Package): Double? = super.getTransitRate(pkg)?.times(multiplier)

}