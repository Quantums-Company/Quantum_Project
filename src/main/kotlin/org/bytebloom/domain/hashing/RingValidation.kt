package org.bytebloom.domain.hashing

data class VehicleValidation(
    val slot: Int,
    val packageCountBefore: Int,
    val packageCountAfter: Int,
    val isBroken: Boolean,
    val nonMigrationPassed: Boolean
)

data class ValidationReport(
    val brokenSlot: Int,
    val reroutedPackages: Int,
    val vehicles: List<VehicleValidation>,
    val allChecksPassed: Boolean
)

fun createValidationReport(
    beforeSnapshot: Map<Int, List<String>>,
    afterSnapshot: Map<Int, List<String>>,
    brokenSlot: Int
): ValidationReport {

    val validations = mutableListOf<VehicleValidation>()

    var passed =  validations
        .all {
            it.isBroken || it.nonMigrationPassed
        }

    val reroutedPackages =
        beforeSnapshot
            .getOrDefault(brokenSlot, emptyList())
            .size

    val allSlots =
        beforeSnapshot.keys
            .union(afterSnapshot.keys)
            .sorted()

    for (slot in allSlots) {

        if (slot == brokenSlot) {

            validations += VehicleValidation(
                slot = slot,
                packageCountBefore = beforeSnapshot[slot]?.size ?: 0,
                packageCountAfter = afterSnapshot[slot]?.size ?: 0,
                isBroken = true,
                nonMigrationPassed = true
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
            nonMigrationPassed = unchanged
        )
    }

    return ValidationReport(
        brokenSlot = brokenSlot,
        reroutedPackages = reroutedPackages,
        vehicles = validations,
        allChecksPassed = passed
    )
}
