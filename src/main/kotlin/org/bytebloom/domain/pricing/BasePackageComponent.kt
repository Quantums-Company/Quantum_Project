package org.bytebloom.domain.pricing

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Route

class BasePackageComponent (
    private val packageData : Package,
    private val availableRoutes : List<Route>,
    private val pricingEngine: RoutePricingEngine
) : PackageComponent {

    override fun getPackage() : Package {
        return packageData
    }

    override fun getTransitRate() : Double {
        return pricingEngine.calculateShippingCost(packageData,
            availableRoutes
        )
    }
}