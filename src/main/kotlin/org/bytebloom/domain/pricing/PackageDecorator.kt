package org.bytebloom.domain.pricing

import org.bytebloom.domain.model.Package

abstract class PackageDecorator(private val component: PackageComponent) : PackageComponent {
    override fun getPackage(): Package {
        return component.getPackage()
    }

    override fun getTransitRate(): Double {
        return component.getTransitRate()
    }
}