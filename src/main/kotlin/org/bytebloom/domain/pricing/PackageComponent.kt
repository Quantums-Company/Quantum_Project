package org.bytebloom.domain.pricing

import org.bytebloom.domain.model.Package
interface PackageComponent {
    fun getPackage() : Package
    fun getTransitRate() : Double
}