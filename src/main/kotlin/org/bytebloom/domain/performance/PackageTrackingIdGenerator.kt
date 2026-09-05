package org.bytebloom.domain.performance

class PackageTrackingIdGenerator {
    companion object {
        private const val TRACKING_ID_DIGIT_COUNT = 6
    }

    fun generate(count: Int): List<String> =
        (1..count).map { number ->
            "PKG-${number.toString().padStart(TRACKING_ID_DIGIT_COUNT, '0')}"
        }
}