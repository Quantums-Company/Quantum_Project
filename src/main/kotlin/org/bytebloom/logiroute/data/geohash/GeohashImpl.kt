package org.bytebloom.logiroute.data.geohash


import org.bytebloom.logiroute.data.csv.exception.InvalidCharacterException
import org.bytebloom.logiroute.domain.model.result.BoundingBox

class GeohashImpl : Geohash {
    private val base32 = "0123456789bcdefghjkmnpqrstuvwxyz"
    private val decodeMap = base32.withIndex().associate { it.value to it.index }

    override fun encode(lat: Double, lon: Double, precision: Int): String {
        val validLat = lat.coerceIn(-90.0, 90.0)
        val validLon = lon.coerceIn(-180.0, 180.0)

        val finalState = (0 until (precision * 5)).fold(EncodeState()) { state, index ->
            state.next(validLat, validLon, index)
        }

        return finalState.charIndices.map { base32[it] }.joinToString("")
    }
    override fun decodeBounds(hash: String): BoundingBox {
        val finalState = hash.lowercase().fold(DecodeState()) { outerState, char ->
            val charIndex = decodeMap[char] ?: throw InvalidCharacterException(char)

            decodeCharacter(outerState, charIndex)
        }
        return BoundingBox(finalState.bounds.minLat, finalState.bounds.maxLat, finalState.bounds.minLon, finalState.bounds.maxLon)
    }

    private fun decodeCharacter(
        outerState: DecodeState,
        charIndex: Int
    ): DecodeState = (4 downTo 0).fold(outerState) { innerState, bitIdx ->
        val extractedBit = (charIndex shr bitIdx) and 1
        innerState.next(extractedBit)
    }

    override fun neighbors(hash: String): Map<String, String> {
        val currentBounds = decodeBounds(hash)
        val precision = hash.length

        val calculateNeighborHash = calculateNeighborHash(currentBounds, precision)

        return mapOf(
            "N"  to calculateNeighborHash (currentBounds.latSize, 0.0),
            "S"  to calculateNeighborHash (-currentBounds.latSize, 0.0),
            "E"  to calculateNeighborHash (0.0, currentBounds.lonSize),
            "W"  to calculateNeighborHash (0.0, -currentBounds.lonSize),
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
        val neighborLat = (currentBounds.centerLat + latOffset).coerceIn(-90.0, 90.0)
        val rawLon = currentBounds.centerLon + lonOffset
        val neighborLon = when {
            rawLon > 180.0 -> rawLon - 360.0
            rawLon < -180.0 -> rawLon + 360.0
            else -> rawLon
        }
        encode(neighborLat, neighborLon, precision)
    }
}