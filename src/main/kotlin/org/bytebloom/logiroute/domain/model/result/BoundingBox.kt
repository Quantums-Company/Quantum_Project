package org.bytebloom.logiroute.domain.model.result

import org.bytebloom.logiroute.domain.model.result.GeoConstants.BIT_HIGH
import org.bytebloom.logiroute.domain.model.result.GeoConstants.BIT_LOW
import org.bytebloom.logiroute.domain.model.result.GeoConstants.CENTER_DIVISOR
import org.bytebloom.logiroute.domain.model.result.GeoConstants.MAX_LATITUDE
import org.bytebloom.logiroute.domain.model.result.GeoConstants.MAX_LONGITUDE
import org.bytebloom.logiroute.domain.model.result.GeoConstants.MIN_LATITUDE
import org.bytebloom.logiroute.domain.model.result.GeoConstants.MIN_LONGITUDE

data class BoundingBox(
    val minLat: Double = MIN_LATITUDE, val maxLat: Double = MAX_LATITUDE,
    val minLon: Double = MIN_LONGITUDE, val maxLon: Double = MAX_LONGITUDE
) {
    val centerLat get() = (minLat + maxLat) / CENTER_DIVISOR
    val centerLon get() = (minLon + maxLon) / CENTER_DIVISOR
    val latSize get() = maxLat - minLat
    val lonSize get() = maxLon - minLon

    fun update(isEven: Boolean, nextBit: Int): BoundingBox {
        return when {
            isEven && nextBit == BIT_HIGH -> copy(minLon = centerLon)
            isEven && nextBit == BIT_LOW -> copy(maxLon = centerLon)
            !isEven && nextBit == BIT_HIGH -> copy(minLat = centerLat)
            else -> copy(maxLat = centerLat)
        }
    }
}