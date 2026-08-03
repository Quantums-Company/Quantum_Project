package org.bytebloom.domain.model.logic
import kotlin.math.abs

class PackageDistributionRing(val circleSize: Int = 100) {

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

    fun assignSlotsDynamically(vehicleIds: List<String>): List<RingVehicle> {
        if (vehicleIds.isEmpty()) return emptyList()
        val distance = circleSize / vehicleIds.size
        return vehicleIds.mapIndexed { index, id ->
            RingVehicle(id = id, slot = index * distance)
        }
    }

    fun mapPackageToSlot(packageId: String): Int {
        return abs(packageId.hashCode()) % circleSize
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
}

fun main() {
    println("\n================== Package Distribution Ring ==================")

    // 1. إنشاء الكائن (Instance) من الكلاس الجديد
    val ringSystem = PackageDistributionRing()

    printSystemInfo(ringSystem)

    // 2. تمرير الكائن لدوال التهيئة
    val vehicles = initializeVehicles(ringSystem)
    val edgeCaseIds = listOf("EDGE-PKG-195", "EDGE-PKG-94", "EDGE-PKG-44")
    val packageIds = edgeCaseIds + (1..30).map { "PKG-SAMPLE-$it" }

    demonstrateEdgeCaseRouting(ringSystem, vehicles, edgeCaseIds)
    runBreakdownSimulation(ringSystem, vehicles, packageIds)

    println("\n=== All requirements verified successfully ===")
}

private fun printSystemInfo(ringSystem: PackageDistributionRing) {
    println("Circle: ${ringSystem.circleSize} slots | Dynamic Vehicle Assignment Active")
}

private fun initializeVehicles(ringSystem: PackageDistributionRing): List<PackageDistributionRing.RingVehicle> {
    // هنا نمرر الـ List التي طلبها المشرف، والكلاس سيوزعها تلقائياً
    val vehicleIds = listOf("TRUCK-A", "TRUCK-B", "TRUCK-C", "TRUCK-D")
    return ringSystem.assignSlotsDynamically(vehicleIds)
}

private fun demonstrateEdgeCaseRouting(ringSystem: PackageDistributionRing, vehicles: List<PackageDistributionRing.RingVehicle>, edgeCaseIds: List<String>) {
    println("\nEdge-case routing (before breakdown):")
    edgeCaseIds.forEach { id ->
        val slot = ringSystem.mapPackageToSlot(id)
        val truck = ringSystem.resolveVehicleClockwise(slot, vehicles)
        println("  $id → slot $slot → ${truck.id} (slot ${truck.slot})")
    }
}

private fun runBreakdownSimulation(ringSystem: PackageDistributionRing, vehicles: List<PackageDistributionRing.RingVehicle>, packageIds: List<String>) {
    val before = ringSystem.distributeAllPackages(packageIds, vehicles)

    val brokenVehicle = vehicles[1]
    val brokenSlot = brokenVehicle.slot

    val remaining = ringSystem.removeVehicleAtSlot(vehicles, brokenSlot)
    val after = ringSystem.rerouteAfterBreakdown(before, brokenSlot, remaining)

    val activeVehicleSlots = remaining.map { it.slot }.toSet()

    val (allPassed, auditLogs) = auditAllPackages(
        previousDistribution = before,
        currentDistribution = after,
        brokenVehicleSlot = brokenSlot,
        stableSlots = activeVehicleSlots
    )

    val targetRerouteSlot = remaining.firstOrNull { it.slot > brokenSlot }?.slot ?: remaining.first().slot

    val reroutedCount = auditLogs.count { it.contains("Re-routed") && it.startsWith("✓") }
    val stableCount = auditLogs.count { it.contains("Non-migration") && it.startsWith("✓") }

    println("\nBreakdown: removed slot $brokenSlot → cargo re-routes to slot $targetRerouteSlot")
    println("Re-routed packages: $reroutedCount")

    println("\nNon-migration validation:")
    println("  Stable vehicles (slots ${activeVehicleSlots.sorted()}): $stableCount packages unchanged")
    println("  Overall: ${if (allPassed) "PASSED" else "FAILED"}")

    check(allPassed) { "Non-migration validation failed." }
}