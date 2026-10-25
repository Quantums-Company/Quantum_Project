package org.bytebloom.logiroute.data.geohash

import org.bytebloom.logiroute.domain.model.result.BoundingBox

interface Geohash {
    fun encode(lat: Double, lon: Double, precision: Int): String
    fun decodeBounds(hash: String): BoundingBox
    fun neighbors(hash: String): Map<String, String>
}