package org.bytebloom.domain.logic

data class VehicleValidation(
    val slot: Int,
    val packageCountBefore: Int,
    val packageCountAfter: Int,
    val isBroken: Boolean,
    val isPassed: Boolean
)

data class ValidationReport(
    val brokenSlot: Int,
    val reroutedPackages: Int,
    val vehicles: List<VehicleValidation>,
    val passed: Boolean
)

fun createValidationReport(
    beforeSnapshot: Map<Int, List<String>>,
    afterSnapshot: Map<Int, List<String>>,
    brokenSlot: Int
): ValidationReport {

    val validations = mutableListOf<VehicleValidation>()

    var passed = true

    val reroutedPackages =
        beforeSnapshot[brokenSlot]?.size ?: 0

    val allSlots = listOf(15, 40, 65, 90)

    for (slot in allSlots) {

        if (slot == brokenSlot) {

            validations += VehicleValidation(
                slot = slot,
                packageCountBefore = beforeSnapshot[slot]?.size ?: 0,
                packageCountAfter = 0,
                isBroken = true,
                isPassed = true
            )

            continue
        }

        val beforePackages = beforeSnapshot[slot] ?: emptyList()
        val afterPackages = afterSnapshot[slot] ?: emptyList()

        val unchanged = beforePackages == afterPackages

        if (!unchanged)
            passed = false

        validations += VehicleValidation(
            slot = slot,
            packageCountBefore = beforePackages.size,
            packageCountAfter = afterPackages.size,
            isBroken = false,
            isPassed = unchanged
        )
    }

    return ValidationReport(
        brokenSlot = brokenSlot,
        reroutedPackages = reroutedPackages,
        vehicles = validations,
        passed = passed
    )
}
