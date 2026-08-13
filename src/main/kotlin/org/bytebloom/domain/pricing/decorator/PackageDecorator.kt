package org.bytebloom.domain.pricing.decorator

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.pricing.core.PackageComponent

abstract class PackageDecorator(private val component: PackageComponent) : PackageComponent {
    override fun getPackage(): Package {
        return component.getPackage()
    }

    override fun getTransitRate(): Double? {
        return component.getTransitRate()
    }
}