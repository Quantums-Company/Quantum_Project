package org.bytebloom.logiroute.domain.pricing.decorator

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.model.validation.ValidatorError
import org.bytebloom.logiroute.domain.model.validation.ValidatorField
import org.bytebloom.logiroute.domain.pricing.core.PackageComponent

class FragileHandlingDecorator(
    component: PackageComponent,
    private val fee: Double
) : PackageDecorator(component) {

    init {
        if (fee < 0.0) {
            throw EntityValidationException(listOf(ValidatorError.NegativeValue(ValidatorField.FEE)))
        }
    }

    override suspend fun getTransitRate(pkg: Package): Double? = super.getTransitRate(pkg)?.plus(fee)
}
