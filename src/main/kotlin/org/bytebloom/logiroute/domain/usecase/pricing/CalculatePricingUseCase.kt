package org.bytebloom.logiroute.domain.usecase.pricing

import org.bytebloom.logiroute.domain.model.exception.EntityValidationException
import org.bytebloom.logiroute.domain.model.exception.UnknownDataException
import org.bytebloom.logiroute.domain.model.validation.ValidatorError
import org.bytebloom.logiroute.domain.model.validation.ValidatorField
import org.bytebloom.logiroute.domain.pricing.core.BasePackageComponent
import org.bytebloom.logiroute.domain.pricing.core.PackageComponent
import org.bytebloom.logiroute.domain.pricing.core.PricingOptions
import org.bytebloom.logiroute.domain.pricing.core.RoutePricingEngine
import org.bytebloom.logiroute.domain.pricing.factory.DecoratorFactory
import org.bytebloom.logiroute.domain.pricing.factory.StrategyFactory
import org.bytebloom.logiroute.domain.repository.RouteRepository

class CalculatePricingUseCase(
    private val strategyFactory: StrategyFactory,
    private val decoratorFactory: DecoratorFactory,
    private val routeRepository: RouteRepository
) {
    suspend operator fun invoke(options: PricingOptions): Double {
        val strategy = strategyFactory.getStrategy(options.strategy)
            ?: throw EntityValidationException(
                listOf(ValidatorError.Custom(ValidatorField.ENTITY, "Unknown pricing strategy: ${options.strategy}"))
            )

        val pricingEngine = RoutePricingEngine(strategy, routeRepository)
        var finalComponent: PackageComponent = BasePackageComponent(pricingEngine)

        options.decorators.forEach { decoratorFee ->
            decoratorFactory.createDecorator(finalComponent, decoratorFee)?.let { finalComponent = it }
        }

        return finalComponent.getTransitRate(options.pkg)
            ?: throw UnknownDataException("Could not calculate a transit rate")
    }
}