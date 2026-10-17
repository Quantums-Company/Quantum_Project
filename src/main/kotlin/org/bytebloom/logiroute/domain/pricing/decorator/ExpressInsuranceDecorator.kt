package org.bytebloom.logiroute.domain.pricing.decorator

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.model.validation.ValidatorError
import org.bytebloom.logiroute.domain.model.validation.ValidatorField
import org.bytebloom.logiroute.domain.pricing.core.PackageComponent

class ExpressInsuranceDecorator(
    component: PackageComponent,
    private val premium: Double
) : PackageDecorator(component) {

    init {
        if (premium < 0.0) {
            throw EntityValidationException(listOf(ValidatorError.NegativeValue(ValidatorField.PREMIUM)))
        }
    }

    override suspend fun getTransitRate(pkg: Package): Double? = super.getTransitRate(pkg)?.plus(premium)

}