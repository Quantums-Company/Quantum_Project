package org.bytebloom.logiroute.data.geohash

import org.bytebloom.logiroute.domain.model.result.BoundingBox

data class EncodeState(
    val bounds: BoundingBox = BoundingBox(),
    val charIndex: Int = 0,
    val charIndices: List<Int> = emptyList()
) {
    fun next(lat: Double, lon: Double, step: Int): EncodeState {
        val isEven = step % 2 == 0
        val midLon = bounds.centerLon
        val midLat = bounds.centerLat

        val nextBit = when {
            isEven && lon >= midLon -> 1
            !isEven && lat >= midLat -> 1
            else -> 0
        }

        val updatedCh = (charIndex shl 1) or nextBit
        val isCharBoundary = (step + 1) % 5 == 0

        val nextBounds = bounds.update(isEven, nextBit)
        val nextCharIndex = if (isCharBoundary) 0 else charIndex
        val nextCharIndices = if (isCharBoundary) charIndices + updatedCh else charIndices
        return EncodeState(bounds = nextBounds, charIndex = nextCharIndex, charIndices = nextCharIndices)
    }
}