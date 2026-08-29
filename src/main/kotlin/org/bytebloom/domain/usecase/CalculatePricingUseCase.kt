package org.bytebloom.domain.usecase

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.pricing.core.PackageComponent
import org.bytebloom.domain.pricing.decorator.ColdChainDecorator
import org.bytebloom.domain.pricing.decorator.ExpressInsuranceDecorator
import org.bytebloom.domain.pricing.decorator.FragileHandlingDecorator

class CalculatePricingUseCase(
    private val baseComponent: PackageComponent
) {

    operator fun invoke(
        pkg: Package,
        isFragile: Boolean,
        requiresColdChain: Boolean,
        isExpress: Boolean,
        fragileFee: Double,
        coldChainMultiplier: Double,
        expressPremium: Double
    ): Double? {

        val pricingConditions = listOf(
            isFragile to { component: PackageComponent -> FragileHandlingDecorator(component, fragileFee) },
            requiresColdChain to { component: PackageComponent -> ColdChainDecorator(component, coldChainMultiplier) },
            isExpress to { component: PackageComponent -> ExpressInsuranceDecorator(component, expressPremium) }
        )

        val finalPricedComponent: PackageComponent = pricingConditions
            .filter { (condition, _) -> condition }
            .fold(baseComponent) { currentComponent, (_, decoratorFactory) ->
                decoratorFactory(currentComponent)
            }

        return finalPricedComponent.getTransitRate(pkg)
    }
}