package org.bytebloom.logiroute.domain.model.result

data class BoundingBox(
    val minLat: Double = -90.0, val maxLat: Double = 90.0,
    val minLon: Double = -180.0, val maxLon: Double = 180.0
) {
    val centerLat get() = (minLat + maxLat) / 2
    val centerLon get() = (minLon + maxLon) / 2
    val latSize get() = maxLat - minLat
    val lonSize get() = maxLon - minLon

    fun update(isEven: Boolean, nextBit: Int): BoundingBox {
        return when {
            isEven && nextBit == 1 -> copy(minLon = centerLon)
            isEven && nextBit == 0 -> copy(maxLon = centerLon)
            !isEven && nextBit == 1 -> copy(minLat = centerLat)
            else -> copy(maxLat = centerLat)
        }
    }
}