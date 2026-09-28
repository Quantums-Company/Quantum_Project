package org.bytebloom.domain.model

enum class EntityType(
    val idPrefix: String
) {
    WAREHOUSE("WH-"),
    PACKAGE("PKG-"),
    ROUTE("RT-"),
    VEHICLE("TRK-")
}