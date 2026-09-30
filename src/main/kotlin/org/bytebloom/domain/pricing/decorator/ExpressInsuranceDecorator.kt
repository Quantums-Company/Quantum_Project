package org.bytebloom.domain.pricing.decorator

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField
import org.bytebloom.domain.pricing.core.PackageComponent

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