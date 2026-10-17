package org.bytebloom.logiroute.domain.pricing.decorator

import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.pricing.core.PackageComponent

abstract class PackageDecorator(private val component: PackageComponent) : PackageComponent {
    override suspend fun getTransitRate(pkg: Package): Double? {
        return component.getTransitRate(pkg)
    }
}