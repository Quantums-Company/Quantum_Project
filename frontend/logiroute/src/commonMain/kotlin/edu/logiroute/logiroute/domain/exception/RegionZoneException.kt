package edu.logiroute.logiroute.domain.exception

open class RegionZoneException(message: String) : Exception(message) {

    companion object {
        const val ERROR_MISSING_ZONE = "Validation Failed: Regional zone value cannot be null or blank."
        const val ERROR_INVALID_ZONE_TEMPLATE = "Validation Failed: '%s' is an unknown or unsupported regional zone."
    }
}

class MissingRegionZoneException :
    RegionZoneException(ERROR_MISSING_ZONE)

class InvalidRegionZoneException(val invalidValue: String) :
    RegionZoneException(ERROR_INVALID_ZONE_TEMPLATE.format(invalidValue))