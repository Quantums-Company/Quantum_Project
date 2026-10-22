package org.bytebloom.logiroute.presentation.presenter

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.exception.ResourceNotFoundException
import org.bytebloom.logiroute.domain.pricing.core.PricingOptions
import org.bytebloom.logiroute.domain.pricing.decorator.DecoratorFee
import org.bytebloom.logiroute.domain.pricing.strategy.ShippingStrategyType
import org.bytebloom.logiroute.domain.usecase.packages.GetAllPackagesUseCase
import org.bytebloom.logiroute.domain.usecase.pricing.CalculatePricingUseCase
import org.bytebloom.logiroute.presentation.screens.PricingScreen

class PricingPresenter(
    private val getAllPackages: GetAllPackagesUseCase,
    private val calculatePricing: CalculatePricingUseCase,
    view: PricingScreen,
    parentScope: CoroutineScope
) : BasePresenter<PricingScreen>(view, parentScope) {

    @Volatile
    private var packages: List<Package> = emptyList()

    fun onViewReady(): Job = perform(task = { getAllPackages().also { packages = it } }) { view.showPackages(it) }

    fun onCalculate(
        packageId: String,
        strategy: ShippingStrategyType,
        decorators: List<DecoratorFee>
    ): Job = perform(
        task = {
            val pkg = packages.firstOrNull { it.id.equals(packageId.trim(), ignoreCase = true) }
                ?: throw ResourceNotFoundException("Package '$packageId' was not found.")
            pkg to calculatePricing(PricingOptions(pkg, strategy, decorators))
        }
    ) { (pkg, price) -> view.showPrice(pkg, strategy, decorators, price) }
}
