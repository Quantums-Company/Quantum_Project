package org.bytebloom.logiroute.domain.pricing.core

import org.bytebloom.logiroute.domain.model.Package

interface PricingEngine {
    suspend fun calculateShippingCost(pkg: Package): Double?
}