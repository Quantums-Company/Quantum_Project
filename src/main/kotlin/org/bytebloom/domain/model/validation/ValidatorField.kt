package org.bytebloom.domain.model.validation

enum class ValidatorField(val displayName: String) {
    ID("ID"),
    NAME("Name"),
    REGIONAL_ZONE("Regional zone"),
    LATITUDE("Latitude"),
    LONGITUDE("Longitude"),
    WEIGHT("Weight"),
    PRIORITY("Priority"),
    DISTANCE_KM("Distance"),
    TYPICAL_DELAY_MIN("Typical delay"),
    MAX_CAPACITY_KG("Maximum capacity"),
    COST_PER_KM("Cost per kilometer"),
    ORIGIN_WAREHOUSE("Origin warehouse"),
    DESTINATION_WAREHOUSE("Destination warehouse"),
    CURRENT_WAREHOUSE("Current warehouse"),
    ENTITY("Entity"),
    FEE("Fee"),
    MULTIPLIER("multiplier"),
    PREMIUM("premium"),
    VOLUME_M3("volumeM3"),
    MAX_VOLUME_M3("Maximum volume")
}