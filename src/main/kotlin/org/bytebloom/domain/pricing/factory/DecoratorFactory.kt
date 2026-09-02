package org.bytebloom.domain.pricing.factory

import org.bytebloom.domain.pricing.core.PackageComponent
import org.bytebloom.domain.pricing.decorator.DecoratorFee
import org.bytebloom.domain.pricing.decorator.DecoratorType

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
