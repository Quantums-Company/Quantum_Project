package org.bytebloom.logiroute.domain.pricing.core

import org.bytebloom.logiroute.domain.model.Package

interface PackageComponent {
    suspend fun getTransitRate(pkg: Package): Double?
}