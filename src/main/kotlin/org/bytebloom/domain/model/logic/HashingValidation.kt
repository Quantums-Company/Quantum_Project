package org.bytebloom.domain.model.logic

import org.bytebloom.domain.model.logic.PackageDistributionRing.PackageMapping

data class ValidationReport(
    val allPassed: Boolean,
    val stableCount: Int,
    val reroutedCount: Int,
    val stableSlots: Set<Int>
)

fun validateNonMigration(
    previousDistribution: Map<String, PackageMapping>,
    currentDistribution: Map<String, PackageMapping>,
    brokenVehicleSlot: Int,
    activeVehicleSlots: Set<Int>,
): ValidationReport {
    val stableSlots = activeVehicleSlots - brokenVehicleSlot

    val isSystemStable = checkSystemStability(
        previousDistribution,
        currentDistribution,
        brokenVehicleSlot,
        stableSlots
    )

    val stableCount = previousDistribution.values.count { it.vehicleSlot in stableSlots }
    val reroutedCount = previousDistribution.values.count { it.vehicleSlot == brokenVehicleSlot }

    return ValidationReport(
        allPassed = isSystemStable,
        stableCount = stableCount,
        reroutedCount = reroutedCount,
        stableSlots = stableSlots
    )
}

private fun checkSystemStability(
    previousDistribution: Map<String, PackageMapping>,
    currentDistribution: Map<String, PackageMapping>,
    brokenVehicleSlot: Int,
    stableSlots: Set<Int>
): Boolean {
    return previousDistribution.all { (packageId, previous) ->
        val current = currentDistribution.getValue(packageId)

        if (previous.vehicleSlot == brokenVehicleSlot) {
            current.vehicleSlot != brokenVehicleSlot
        } else if (previous.vehicleSlot in stableSlots) {
            current == previous
        } else {
            true
        }
    }
}

// أضفنا دالة التدقيق هنا كدالة عامة لتتناسق مع باقي دوال الملف
fun auditAllPackages(
    previousDistribution: Map<String, PackageMapping>,
    currentDistribution: Map<String, PackageMapping>,
    brokenVehicleSlot: Int,
    stableSlots: Set<Int>
): Pair<Boolean, List<String>> {
    var isSystemStable = true
    val auditLogs = mutableListOf<String>()

    previousDistribution.forEach { (packageId, previous) ->
        val current = currentDistribution.getValue(packageId)

        if (previous.vehicleSlot == brokenVehicleSlot) {
            val isProperlyRerouted = current.vehicleSlot != brokenVehicleSlot
            if (!isProperlyRerouted) isSystemStable = false
            auditLogs += "${if (isProperlyRerouted) "✓" else "✗"} Re-routed '$packageId' from slot $brokenVehicleSlot → slot ${current.vehicleSlot}"

        } else if (previous.vehicleSlot in stableSlots) {
            val isUnchanged = current == previous
            if (!isUnchanged) isSystemStable = false
            auditLogs += "${if (isUnchanged) "✓" else "✗"} Non-migration: '$packageId' stays on vehicle slot ${previous.vehicleSlot}"
        }
    }

    return Pair(isSystemStable, auditLogs)
}