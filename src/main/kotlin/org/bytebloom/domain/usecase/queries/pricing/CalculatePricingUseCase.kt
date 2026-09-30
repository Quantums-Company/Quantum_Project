package org.bytebloom.domain.usecase.queries.pricing

import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.domain.model.exception.UnknownDataException
import org.bytebloom.domain.model.validation.ValidatorError
import org.bytebloom.domain.model.validation.ValidatorField
import org.bytebloom.domain.pricing.core.BasePackageComponent
import org.bytebloom.domain.pricing.core.PackageComponent
import org.bytebloom.domain.pricing.core.PricingOptions
import org.bytebloom.domain.pricing.core.RoutePricingEngine
import org.bytebloom.domain.pricing.factory.DecoratorFactory
import org.bytebloom.domain.pricing.factory.StrategyFactory
import org.bytebloom.domain.repository.RouteRepository

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