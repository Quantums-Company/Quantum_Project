package org.bytebloom.domain.validation

enum class EntityType(
    val idPrefix: String
) {
    WAREHOUSE("WH-"),
    PACKAGE("PKG-"),
    ROUTE("RT-"),
    VEHICLE("TRK-")
}