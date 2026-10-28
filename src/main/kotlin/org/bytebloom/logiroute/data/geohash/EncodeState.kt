package org.bytebloom.logiroute.data.geohash

import org.bytebloom.logiroute.data.geohash.GeoConstants.BITS_PER_CHAR
import org.bytebloom.logiroute.data.geohash.GeoConstants.BIT_SHIFT_AMOUNT
import org.bytebloom.logiroute.data.geohash.GeoConstants.INITIAL_CHAR_INDEX
import org.bytebloom.logiroute.data.geohash.GeoConstants.STEP_PARITY_DIVISOR
import org.bytebloom.logiroute.domain.model.result.BoundingBox
import org.bytebloom.logiroute.domain.model.result.GeoConstants.BIT_HIGH
import org.bytebloom.logiroute.domain.model.result.GeoConstants.BIT_LOW

data class EncodeState(
    val bounds: BoundingBox = BoundingBox(),
    val charIndex: Int = 0,
    val charIndices: List<Int> = emptyList()
) {
    fun next(lat: Double, lon: Double, step: Int): EncodeState {
        val isEven = step % STEP_PARITY_DIVISOR == 0
        val midLon = bounds.centerLon
        val midLat = bounds.centerLat

        val nextBit = when {
            isEven && lon >= midLon -> BIT_HIGH
            !isEven && lat >= midLat -> BIT_HIGH
            else -> BIT_LOW
        }

        val updatedCh = (charIndex shl BIT_SHIFT_AMOUNT) or nextBit
        val isCharBoundary = (step + 1) % BITS_PER_CHAR  == 0

        val nextBounds = bounds.update(isEven, nextBit)
        val nextCharIndex = if (isCharBoundary) INITIAL_CHAR_INDEX else charIndex
        val nextCharIndices = if (isCharBoundary) charIndices + updatedCh else charIndices
        return EncodeState(bounds = nextBounds, charIndex = nextCharIndex, charIndices = nextCharIndices)
    }
}