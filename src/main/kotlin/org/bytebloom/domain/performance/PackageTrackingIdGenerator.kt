package org.bytebloom.domain.performance

class PackageTrackingIdGenerator {

    fun generate(count: Int): List<String> =
        (1..count).map { number ->
            "PKG-${number.toString().padStart(6, '0')}"
        }
}