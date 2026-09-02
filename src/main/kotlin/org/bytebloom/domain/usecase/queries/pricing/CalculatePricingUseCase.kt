package org.bytebloom.domain.usecase.queries.pricing

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.pricing.core.BasePackageComponent
import org.bytebloom.domain.pricing.core.PackageComponent
import org.bytebloom.domain.pricing.core.PricingOptions
import org.bytebloom.domain.pricing.core.RoutePricingEngine
import org.bytebloom.domain.pricing.factory.DecoratorFactory
import org.bytebloom.domain.pricing.factory.StrategyFactory
import org.bytebloom.domain.repository.RouteRepository
import org.bytebloom.util.Logger

class CalculatePricingUseCase(
    private val strategyFactory: StrategyFactory,
    private val decoratorFactory: DecoratorFactory,
    private val routeRepository: RouteRepository
) {
    operator fun invoke(options: PricingOptions): Double? {

        val strategy = strategyFactory.getStrategy(options.strategy)

        if(strategy == null){
            Logger.error("Unknown strategy: ${options.strategy}")
            return null
        }

        val pricingEngine = RoutePricingEngine(strategy, routeRepository)
        var finalComponent: PackageComponent = BasePackageComponent(pricingEngine)

        options.decorators.forEach { decoratorFee ->
            val decorator = decoratorFactory.createDecorator(
                finalComponent,
                decoratorFee
            )

            decorator?.let { finalComponent = it }
        }

        return finalComponent.getTransitRate(options.pkg)
    }
}