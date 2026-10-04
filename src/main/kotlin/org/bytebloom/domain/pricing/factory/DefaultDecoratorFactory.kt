package org.bytebloom.domain.pricing.factory

import org.bytebloom.domain.pricing.core.PackageComponent
import org.bytebloom.domain.pricing.decorator.ColdChainDecorator
import org.bytebloom.domain.pricing.decorator.DecoratorFee
import org.bytebloom.domain.pricing.decorator.DecoratorType
import org.bytebloom.domain.pricing.decorator.ExpressInsuranceDecorator
import org.bytebloom.domain.pricing.decorator.FragileHandlingDecorator

class DefaultDecoratorFactory : DecoratorFactory {
    private val decorators = mutableMapOf<DecoratorType, (PackageComponent, Double) -> PackageComponent>()

    init {
        registerDecorator(DecoratorType.COLD_CHAIN) { component, multiplier ->
            ColdChainDecorator(component, multiplier)
        }

        registerDecorator(DecoratorType.EXPRESS_INSURANCE) { component, premium ->
            ExpressInsuranceDecorator(component, premium)
        }

        registerDecorator(DecoratorType.FRAGILE_HANDLING) { component, fee ->
            FragileHandlingDecorator(component, fee)
        }
    }

    override fun createDecorator(
        component: PackageComponent,
        decoratorFee: DecoratorFee
    ): PackageComponent? {
        val creator = decorators[decoratorFee.type]
        return creator?.invoke(component, decoratorFee.value)
    }

    override fun registerDecorator(
        type: DecoratorType,
        creator: (PackageComponent, Double) -> PackageComponent
    ) {
        decorators[type] = creator
    }
}