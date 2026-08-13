package org.bytebloom.domain.pricing

class ExpressInsuranceDecorator(
    component: PackageComponent,
    private val premium: Double
) : PackageDecorator(component) {
    
    override fun getTransitRate(): Double {
        return super.getTransitRate() + premium
    }
}
