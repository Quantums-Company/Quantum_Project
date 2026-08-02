package org.bytebloom.logic
import kotlin.math.abs

object PackageDistributionRing {

    const val CIRCLE_SIZE = 100
    const val SLOT_TRUCK_A = 15
    const val SLOT_TRUCK_B = 40
    const val SLOT_TRUCK_C = 65
    const val SLOT_TRUCK_D = 90

    val DEFAULT_VEHICLE_SLOTS = setOf(SLOT_TRUCK_A, SLOT_TRUCK_B, SLOT_TRUCK_C, SLOT_TRUCK_D)

    data class RingVehicle(
        val id: String,
        val slot: Int,
    )

    data class PackageMapping(
        val packageId: String,
        val circleSlot: Int,
        val vehicleSlot: Int,
        val vehicleId: String,
    )

    data class ValidationReport(
        val allPassed: Boolean,
        val lines: List<String>,
        val stableCount: Int,
        val reroutedCount: Int,
        val stableSlots: Set<Int>
    )

    fun mapPackageToSlot(packageId: String): Int {
        return abs(packageId.hashCode()) % CIRCLE_SIZE
    }

    fun resolveVehicleClockwise(
        packageSlot: Int,
        vehicles: List<RingVehicle>,
    ): RingVehicle {
        val sortedBySlot = vehicles.sortedBy { it.slot }
        val directMatch = sortedBySlot.firstOrNull { it.slot >= packageSlot }
        return directMatch ?: sortedBySlot.first()
    }

    fun distributeAllPackages(
        packageIds: List<String>,
        vehicles: List<RingVehicle>,
    ): Map<String, PackageMapping> {
        return packageIds.associateWith { packageId ->
            val slot = mapPackageToSlot(packageId)
            val vehicle = resolveVehicleClockwise(slot, vehicles)
            PackageMapping(
                packageId = packageId,
                circleSlot = slot,
                vehicleSlot = vehicle.slot,
                vehicleId = vehicle.id,
            )
        }
    }

    fun removeVehicleAtSlot(
        vehicles: List<RingVehicle>,
        brokenSlot: Int,
    ): List<RingVehicle> {
        return vehicles.filterNot { it.slot == brokenSlot }
    }

    fun rerouteAfterBreakdown(
        previousDistribution: Map<String, PackageMapping>,
        brokenVehicleSlot: Int,
        remainingVehicles: List<RingVehicle>,
    ): Map<String, PackageMapping> {
        return previousDistribution.mapValues { (_, mapping) ->
            if (mapping.vehicleSlot != brokenVehicleSlot) {
                mapping
            } else {
                val newVehicle = resolveVehicleClockwise(mapping.circleSlot, remainingVehicles)
                mapping.copy(
                    vehicleSlot = newVehicle.slot,
                    vehicleId = newVehicle.id,
                )
            }
        }
    }

    fun validateNonMigration(
        previousDistribution: Map<String, PackageMapping>,
        currentDistribution: Map<String, PackageMapping>,
        brokenVehicleSlot: Int,
        activeVehicleSlots: Set<Int>,
    ): ValidationReport {
        val stableSlots = activeVehicleSlots - brokenVehicleSlot

        val (isSystemStable, auditLogs) = auditAllPackages(
            previousDistribution,
            currentDistribution,
            brokenVehicleSlot,
            stableSlots
        )

        val stableCount = previousDistribution.values.count { it.vehicleSlot in stableSlots }
        val reroutedCount = previousDistribution.values.count { it.vehicleSlot == brokenVehicleSlot }

        return ValidationReport(
            allPassed = isSystemStable,
            lines = auditLogs,
            stableCount = stableCount,
            reroutedCount = reroutedCount,
            stableSlots = stableSlots
        )
    }

    private fun auditAllPackages(
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
}
fun main() {
    val ring = PackageDistributionRing
    val vehicles = listOf(
        PackageDistributionRing.RingVehicle("TRUCK-A", PackageDistributionRing.SLOT_TRUCK_A),
        PackageDistributionRing.RingVehicle("TRUCK-B", PackageDistributionRing.SLOT_TRUCK_B),
        PackageDistributionRing.RingVehicle("TRUCK-C", PackageDistributionRing.SLOT_TRUCK_C),
        PackageDistributionRing.RingVehicle("TRUCK-D", PackageDistributionRing.SLOT_TRUCK_D),
    )

    val edgeCaseIds = listOf("EDGE-PKG-195", "EDGE-PKG-94", "EDGE-PKG-44")
    val packageIds = edgeCaseIds + (1..30).map { "PKG-SAMPLE-$it" }

    println("\n================== Package Distribution Ring ==================")
    println("Circle: ${ring.CIRCLE_SIZE} slots | Vehicles at ${ring.DEFAULT_VEHICLE_SLOTS}")

    println("\nEdge-case routing (before breakdown):")
    edgeCaseIds.forEach { id ->
        val slot = ring.mapPackageToSlot(id)
        val truck = ring.resolveVehicleClockwise(slot, vehicles)
        println("  $id → slot $slot → ${truck.id} (slot ${truck.slot})")
    }

    val before = ring.distributeAllPackages(packageIds, vehicles)
    val brokenSlot = PackageDistributionRing.SLOT_TRUCK_B
    val remaining = ring.removeVehicleAtSlot(vehicles, brokenSlot)
    val after = ring.rerouteAfterBreakdown (before, brokenSlot, remaining)
    val report = ring.validateNonMigration(
        before, after, brokenSlot, ring.DEFAULT_VEHICLE_SLOTS,
    )

    println("\nBreakdown: removed slot $brokenSlot → cargo re-routes to slot ${PackageDistributionRing.SLOT_TRUCK_C}")
    println("Re-routed packages: ${report.reroutedCount}")

    println("\nNon-migration validation:")
    println("  Stable vehicles (slots ${report.stableSlots.sorted()}): ${report.stableCount} packages unchanged")
    println("  Overall: ${if (report.allPassed) "PASSED" else "FAILED"}")

    check(report.allPassed) { "Non-migration validation failed." }
    println("\n=== All requirements verified successfully ===")
}
