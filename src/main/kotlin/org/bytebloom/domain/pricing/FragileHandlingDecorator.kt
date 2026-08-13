package org.bytebloom.domain.pricing

class FragileHandlingDecorator(
    component: PackageComponent,
    private val fee: Double
) : PackageDecorator(component) {

    override fun getTransitRate(): Double {
        return super.getTransitRate() + fee
    }
}
