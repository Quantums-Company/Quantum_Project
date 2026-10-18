package org.bytebloom.logiroute.domain.pricing.factory

import org.bytebloom.logiroute.domain.pricing.core.PackageComponent
import org.bytebloom.logiroute.domain.pricing.decorator.DecoratorFee
import org.bytebloom.logiroute.domain.pricing.decorator.DecoratorType

interface DecoratorFactory {
    fun createDecorator(
        component: PackageComponent,
        decoratorFee: DecoratorFee  // نمرر الكائن كامل
    ): PackageComponent?

    fun registerDecorator(
        type: DecoratorType,
        creator: (PackageComponent, Double) -> PackageComponent  // Double بدل Map
    )
}
