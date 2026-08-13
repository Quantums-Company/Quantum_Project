package org.bytebloom.domain.pricing

class ColdChainDecorator(
    component: PackageComponent,
    private val multiplier: Double
) : PackageDecorator(component) {
    
    override fun getTransitRate(): Double {
        return super.getTransitRate() * multiplier
    }
}