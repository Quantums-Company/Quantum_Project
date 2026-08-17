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
    val reroutedPackages =
        beforeSnapshot.getOrDefault(brokenSlot, emptyList()).size

    val validations = buildVehicleValidations(
        beforeSnapshot, afterSnapshot, brokenSlot
    )

    val allChecksPassed = validations.all {
        it.isBroken || it.nonMigrationPassed
    }

    return ValidationReport(
        brokenSlot = brokenSlot,
        reroutedPackages = reroutedPackages,
        vehicles = validations,
        allChecksPassed = allChecksPassed
    )
}

private fun buildVehicleValidations(
    beforeSnapshot: Map<Int, List<String>>,
    afterSnapshot: Map<Int, List<String>>,
    brokenSlot: Int
): List<VehicleValidation> {
    val allSlots = beforeSnapshot.keys
        .union(afterSnapshot.keys)
        .sorted()

    val validations = mutableListOf<VehicleValidation>()

    for (slot in allSlots) {
        if (slot == brokenSlot) {
            validations += createBrokenValidation(slot, beforeSnapshot, afterSnapshot)
        } else {
            validations += createHealthyValidation(slot, beforeSnapshot, afterSnapshot)
        }
    }

    return validations
}

private fun createBrokenValidation(
    slot: Int,
    beforeSnapshot: Map<Int, List<String>>,
    afterSnapshot: Map<Int, List<String>>
): VehicleValidation {
    return VehicleValidation(
        slot = slot,
        packageCountBefore = beforeSnapshot[slot]?.size ?: 0,
        packageCountAfter = afterSnapshot[slot]?.size ?: 0,
        isBroken = true,
        nonMigrationPassed = true
    )
}

private fun createHealthyValidation(
    slot: Int,
    beforeSnapshot: Map<Int, List<String>>,
    afterSnapshot: Map<Int, List<String>>
): VehicleValidation {
    val beforePackages = beforeSnapshot[slot] ?: emptyList()
    val afterPackages = afterSnapshot[slot] ?: emptyList()
    val unchanged = beforePackages == afterPackages

    return VehicleValidation(
        slot = slot,
        packageCountBefore = beforePackages.size,
        packageCountAfter = afterPackages.size,
        isBroken = false,
        nonMigrationPassed = unchanged
    )
}
