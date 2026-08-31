package org.bytebloom.domain.usecase.required

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.pricing.core.PackageComponent
import org.bytebloom.domain.pricing.decorator.ColdChainDecorator
import org.bytebloom.domain.pricing.decorator.ExpressInsuranceDecorator
import org.bytebloom.domain.pricing.decorator.FragileHandlingDecorator

data class PricingOptions(
    val isFragile: Boolean,
    val requiresColdChain: Boolean,
    val isExpress: Boolean,
    val fragileFee: Double,
    val coldChainMultiplier: Double,
    val expressPremium: Double
)

class CalculatePricingUseCase(
    private val baseComponent: PackageComponent
) {
    operator fun invoke(pkg: Package, options: PricingOptions): Double? {

        val pricingConditions = listOf(
            options.isFragile to { component: PackageComponent -> FragileHandlingDecorator(component, options.fragileFee) },
            options.requiresColdChain to { component: PackageComponent -> ColdChainDecorator(component, options.coldChainMultiplier) },
            options.isExpress to { component: PackageComponent -> ExpressInsuranceDecorator(component, options.expressPremium) }
        )

        val finalPricedComponent: PackageComponent = pricingConditions
            .filter { (condition, _) -> condition }
            .fold(baseComponent) { currentComponent, (_, decoratorFactory) ->
                decoratorFactory(currentComponent)
            }

        return finalPricedComponent.getTransitRate(pkg)
    }
}