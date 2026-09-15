package org.bytebloom.domain.pricing.core

import org.bytebloom.domain.model.Package

interface PricingEngine {
    suspend fun calculateShippingCost(pkg: Package): Double?
}