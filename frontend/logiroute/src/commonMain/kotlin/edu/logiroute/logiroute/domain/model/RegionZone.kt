package edu.logiroute.logiroute.domain.model

import edu.logiroute.logiroute.domain.exception.InvalidRegionZoneException
import edu.logiroute.logiroute.domain.exception.MissingRegionZoneException

enum class RegionZone {
    NORTH,
    CENTRAL,
    SOUTH,
    EAST,
    WEST;
    companion object {
        fun from(value: String?): RegionZone {
            if (value.isNullOrBlank()) {
                throw MissingRegionZoneException()
            }

            return when (val normalized = value.trim().uppercase()) {
                "NORTH" -> NORTH
                "CENTRAL" -> CENTRAL
                "SOUTH" -> SOUTH
                "EAST" -> EAST
                "WEST" -> WEST
                else -> throw InvalidRegionZoneException(normalized)
            }
        }
    }
}