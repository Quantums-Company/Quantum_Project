package org.bytebloom.domain.hashing

import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Vehicle
import kotlin.math.abs


class PackageDistributionRing(val packages: List<Package>, val vehicles: List<Vehicle>) {
    private companion object {
        const val CIRCLE_SIZE = 100
        val DEFAULT_SLOTS =
            listOf(15,40,65,90)
    }

    private val _vehicleRing = mutableMapOf<Int, Vehicle>()
    val vehiclesBySlot
        get() = _vehicleRing

    private val _packageSlots = mutableMapOf<Package, Int>()
    val packageSlots
        get() = _packageSlots

    private val _assignments = mutableMapOf<Vehicle, MutableList<Package>>()
    val assignments
        get() = _assignments

    init {
        mapVehiclesToSlots()
        mapPackagesToSlots()
        distributeAllPackages()
    }

    fun mapVehiclesToSlots() {
        DEFAULT_SLOTS
            .zip(vehicles)
            .forEach { (slot, vehicle) ->
                _vehicleRing[slot] = vehicle
            }
    }

    fun mapPackageToSlot(packageId: String): Int {
        return abs(packageId.hashCode()) % CIRCLE_SIZE
    }

    fun mapPackagesToSlots() {
        packages.forEach { pkg ->
            packageSlots[pkg] = mapPackageToSlot(pkg.id)
        }
    }

    fun resolveVehicleClockwise(packageSlot: Int): Vehicle {
        require(_vehicleRing.isNotEmpty()) { "Vehicle ring cannot be empty." }

        _vehicleRing[packageSlot]?.let { return it }

        val sortedSlots = _vehicleRing.keys.sorted()

        for (slot in sortedSlots) {
            if (slot > packageSlot) {
                return _vehicleRing[slot]!!
            }
        }

        return _vehicleRing[sortedSlots.first()]!!
    }


    fun distributeAllPackages() {
        for (vehicle in _vehicleRing.values) {
            _assignments[vehicle] = mutableListOf()
        }

        for ((pkg, slot) in _packageSlots) {
            val targetVehicle = resolveVehicleClockwise(slot)
            _assignments.getValue(targetVehicle).add(pkg)
        }
    }

    fun removeVehicle(slot: Int) {
        val brokenVehicle = _vehicleRing.remove(slot)
        if (brokenVehicle != null) {
            reroutePackages(brokenVehicle)
        }
    }

    private fun reroutePackages(brokenVehicle: Vehicle) {
        val orphanedPackages = _assignments[brokenVehicle] ?: mutableListOf()
        _assignments.remove(brokenVehicle)

        for (pkg in orphanedPackages) {
            val slot = requireNotNull(_packageSlots[pkg])
            val nextVehicle = resolveVehicleClockwise(slot)
            _assignments.getOrPut(nextVehicle) { mutableListOf() }.add(pkg)
        }
    }

    fun createSnapshot(): Map<Int, List<String>> =
        DEFAULT_SLOTS.associateWith { slot ->
            _vehicleRing[slot]
                ?.let { vehicle ->
                    _assignments[vehicle]
                        ?.map(Package::id)
                        ?.sorted()
                }
                ?: emptyList()
        }
}