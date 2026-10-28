package org.bytebloom.logiroute.data.geohash

import org.bytebloom.logiroute.domain.model.result.BoundingBox

data class DecodeState(
    val bounds: BoundingBox = BoundingBox(),
    val isEven: Boolean = true
) {
    fun next(extractedBit: Int): DecodeState {
        val nextBounds = bounds.update(!isEven, extractedBit)
        return DecodeState(bounds = nextBounds, isEven = !isEven)
    }
}