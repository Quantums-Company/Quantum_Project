package org.bytebloom.logiroute.data.geohash


import org.bytebloom.logiroute.data.csv.exception.InvalidCharacterException
import org.bytebloom.logiroute.data.geohash.GeoConstants.BITS_PER_CHAR
import org.bytebloom.logiroute.data.geohash.GeoConstants.BIT_MASK
import org.bytebloom.logiroute.data.geohash.GeoConstants.LONGITUDE_WRAP_SPAN
import org.bytebloom.logiroute.data.geohash.GeoConstants.MAX_BIT_INDEX
import org.bytebloom.logiroute.data.geohash.GeoConstants.ZERO_OFFSET
import org.bytebloom.logiroute.domain.model.result.BoundingBox
import org.bytebloom.logiroute.domain.model.result.GeoConstants.MAX_LATITUDE
import org.bytebloom.logiroute.domain.model.result.GeoConstants.MAX_LONGITUDE
import org.bytebloom.logiroute.domain.model.result.GeoConstants.MIN_LATITUDE
import org.bytebloom.logiroute.domain.model.result.GeoConstants.MIN_LONGITUDE

class GeohashImpl : Geohash {
    private val base32 = "0123456789bcdefghjkmnpqrstuvwxyz"
    private val decodeMap = base32.withIndex().associate { it.value to it.index }

    override fun encode(lat: Double, lon: Double, precision: Int): String {
        val validLat = lat.coerceIn(MIN_LATITUDE, MAX_LATITUDE)
        val validLon = lon.coerceIn(MIN_LONGITUDE, MAX_LONGITUDE)

        val finalState = (0 until (precision * BITS_PER_CHAR)).fold(EncodeState()) { state, index ->
            state.next(validLat, validLon, index)
        }

        return finalState.charIndices.map { base32[it] }.joinToString("")
    }
    override fun decodeBounds(hash: String): BoundingBox {
        val finalState = hash.lowercase().fold(DecodeState()) { outerState, char ->
            val charIndex = decodeMap[char] ?: throw InvalidCharacterException(char)

            decodeCharacter(outerState, charIndex)
        }
        return BoundingBox(
            finalState.bounds.minLat,
            finalState.bounds.maxLat,
            finalState.bounds.minLon,
            finalState.bounds.maxLon
        )
    }

    private fun decodeCharacter(
        outerState: DecodeState,
        charIndex: Int
    ): DecodeState = (MAX_BIT_INDEX downTo 0).fold(outerState) { innerState, bitIdx ->
        val extractedBit = (charIndex shr bitIdx) and BIT_MASK
        innerState.next(extractedBit)
    }

    override fun neighbors(hash: String): Map<String, String> {
        val currentBounds = decodeBounds(hash)
        val precision = hash.length

        val calculateNeighborHash = calculateNeighborHash(currentBounds, precision)

        return mapOf(
            "N"  to calculateNeighborHash (currentBounds.latSize, ZERO_OFFSET),
            "S"  to calculateNeighborHash (-currentBounds.latSize, ZERO_OFFSET),
            "E"  to calculateNeighborHash (ZERO_OFFSET, currentBounds.lonSize),
            "W"  to calculateNeighborHash (ZERO_OFFSET, -currentBounds.lonSize),
            "NE" to calculateNeighborHash (currentBounds.latSize, currentBounds.lonSize),
            "NW" to calculateNeighborHash (currentBounds.latSize, -currentBounds.lonSize),
            "SE" to calculateNeighborHash (-currentBounds.latSize, currentBounds.lonSize),
            "SW" to calculateNeighborHash (-currentBounds.latSize, -currentBounds.lonSize)
        )
    }

    private fun calculateNeighborHash(
        currentBounds: BoundingBox,
        precision: Int
    ): (Double, Double) -> String = { latOffset: Double, lonOffset: Double ->
        val neighborLat = (currentBounds.centerLat + latOffset).coerceIn(MIN_LATITUDE, MAX_LATITUDE)
        val rawLon = currentBounds.centerLon + lonOffset
        val neighborLon = when {
            rawLon > MAX_LONGITUDE -> rawLon - LONGITUDE_WRAP_SPAN
            rawLon < MAX_LONGITUDE -> rawLon + LONGITUDE_WRAP_SPAN
            else -> rawLon
        }
        encode(neighborLat, neighborLon, precision)
    }
}